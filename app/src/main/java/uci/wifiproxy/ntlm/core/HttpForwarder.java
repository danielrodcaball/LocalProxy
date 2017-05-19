package uci.wifiproxy.ntlm.core;

import android.util.Log;

import org.apache.commons.httpclient.ConnectMethod;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.ProxyClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.auth.BasicScheme;
import org.apache.commons.httpclient.auth.DigestScheme;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import uci.wifiproxy.ntlm.ntlmv2.JCIFS_NTLMScheme;

/* *
 * Esta es la clase principal del servidor.
 * Aquí se manejan las conexiones.
 * (No es un Thread!!!)
 * */

public class HttpForwarder extends Thread {

    static List<String> stripHeadersIn = Arrays.asList(new String[]{
            "Content-Type", "Content-Length", "Proxy-Connection"});
    static List<String> stripHeadersOut = Arrays.asList(new String[]{
            "Proxy-Authentication", "Proxy-Authorization"});

    public static final String NTLM_SCHEME = "NTLM";
    public static final String BASIC_SCHEME = "Basic";
    public static final String DIGEST_SCHEME = "Digest";
    private ServerSocket ssocket;
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private HttpClient delegateClient;
    private HttpClient noDelegateClient;
    private int inport;
    private String addr = "";
    private String user;
    private String pass;
    private String domain;
    private String bypass;
    private String authScheme;
    public boolean running = true;
    private LinkedList<Socket> listaSockets = new LinkedList<Socket>();

    private UsernamePasswordCredentials credentials = null;

    public HttpForwarder(String addr, int inport, String domain, String user,
                         String pass, int outport, boolean onlyLocal, String bypass, String authScheme) throws IOException {
        this.addr = addr;
        this.inport = inport;
        this.user = user;
        this.pass = pass;
        this.domain = domain;
        this.bypass = bypass;
        this.authScheme = authScheme;

        if (onlyLocal) {
            this.ssocket = new ServerSocket(outport, 0,
                    InetAddress.getByName("127.0.0.1"));
        } else {
            this.ssocket = new ServerSocket(outport);
        }

        MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
        manager.getParams().setDefaultMaxConnectionsPerHost(40);
        this.delegateClient = new HttpClient(manager);
        this.delegateClient.getHostConfiguration().setProxy(addr, inport);

        //TODO the ntlm scheme cause network job, for this reason it is necessary create the credentials schemes inside the run method
        if (authScheme.equals(NTLM_SCHEME)) {
//            credentials = new NTCredentials(user, pass, InetAddress.getLocalHost()
//                    .getHostName(), domain);
            AuthPolicy.registerAuthScheme(AuthPolicy.NTLM, JCIFS_NTLMScheme.class);
        } else if (authScheme.equals(BASIC_SCHEME)) {
            credentials = new UsernamePasswordCredentials(user, pass);
            AuthPolicy.registerAuthScheme(AuthPolicy.BASIC, BasicScheme.class);
        } else if (authScheme.equals(DIGEST_SCHEME)) {
            credentials = new UsernamePasswordCredentials(user, pass);
            AuthPolicy.registerAuthScheme(AuthPolicy.DIGEST, DigestScheme.class);
        } else {
            System.out.println("There is no authentication scheme selected");
            return;
        }

        Log.e(getClass().getName(), "Starting proxy with " + authScheme + " scheme");

        this.noDelegateClient = new HttpClient(manager);
    }

    public void run() {
        if (authScheme.equals(NTLM_SCHEME)) {
            try {
                credentials = new NTCredentials(user, pass, InetAddress.getLocalHost()
                        .getHostName(), domain);
            } catch (UnknownHostException e) {
                Log.e(getClass().getName(), e.getMessage());
            }
        }
        this.delegateClient.getState().setProxyCredentials(
                new AuthScope(AuthScope.ANY), credentials);
        Log.e(getClass().getName(), "Proxy started");
        while (running) {
            try {
                if (interrupted()) {
                    Log.e(getClass().getName(), "The proxy task was interrupted");
                }
                Socket s = this.ssocket.accept();
                listaSockets.add(s);
                this.threadPool.execute(new Handler(s));
            } catch (IOException e) {
                System.out.print(e.getMessage());
            }
        }
    }

    public void halt() {
        Log.e(getClass().getName(), "Stoping proxy");
        running = false;
        terminate();
    }

    public void terminate() {
        /*
        *TODO: look for doc about java.util.ConcurrentModificationException
        *this method crashes sometimes trying to access the list
        * */
        try {
            for (Socket a : listaSockets) {
                try {
                    a.close();
                } catch (Exception ex) {
                    Log.e("Error closing socket", ex.getMessage());
                }
            }
            listaSockets.clear();

        } catch (java.util.ConcurrentModificationException concurrentModificationException) {
            Log.e(HttpForwarder.class.getName(), concurrentModificationException.getMessage());
        } finally {
            try {
                this.close();
            } catch (IOException e) {
                Log.e(getClass().getName(), "Error closing server socket:" + e.getMessage());
            }
            this.running = false;
            if (authScheme.equals(NTLM_SCHEME)) {
                AuthPolicy.unregisterAuthScheme(AuthPolicy.NTLM);
            } else if (authScheme.equals(BASIC_SCHEME)) {
                AuthPolicy.unregisterAuthScheme(AuthPolicy.BASIC);
            } else if (authScheme.equals(DIGEST_SCHEME)) {
                AuthPolicy.unregisterAuthScheme(AuthPolicy.DIGEST);
            }
        }
    }

    public void close() throws IOException {
        this.ssocket.close();
    }

    void doConnectNoProxy(HttpParser parser, OutputStream os) {
        Socket remoteSocket = null;
        try {
            Log.i("making connection", parser.getUri());
            String[] uri = parser.getUri().split(":");
            InputStream in = null;
            OutputStream out = null;
            try {
                remoteSocket = new Socket(uri[0], Integer.parseInt(uri[1]));
                in = remoteSocket.getInputStream();
                out = remoteSocket.getOutputStream();
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            os.write("HTTP/1.0 200 Connection established".getBytes());
            os.write("\r\n\r\n".getBytes());
            this.threadPool.execute(new Piper(parser, out));

//            BufferedReader i = new BufferedReader(
//                    new InputStreamReader(in));
//            String line = null;
//            while ((line = i.readLine()) != null) {
//                Log.e("InputStream", line);
//            }


            new Piper(in, os).run();
            Log.e("paso", "OK");
            parser.close();
            os.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("transport error", e.getMessage());
            e.printStackTrace();
        } finally {
            if (remoteSocket != null) {
                try {
                    remoteSocket.close();
                } catch (Exception fe) {
                }
            }
            try {
                os.close();
            } catch (IOException e) {
            }
            try {
                parser.close();
            } catch (IOException e) {
            }

        }
    }

//    void doConnect(HttpParser parser, OutputStream os) {
//        Log.i("making connection", parser.getUri());
//        String[] uri = parser.getUri().split(":");
//        Socket proxySocket = null;
//        try {
//            Log.i("making connection", parser.getUri());
//            InputStream in = null;
//            OutputStream out = null;
//            try {
//                proxySocket = GetSocket(uri[0], uri[1]);
//                in = proxySocket.getInputStream();
//                out = proxySocket.getOutputStream();
//            } catch (UnknownHostException e) {
//                Log.e(getClass().getName(), e.getMessage());
//            } catch (IOException e) {
//                Log.e(getClass().getName(), e.getMessage());
//            }
//
//            os.write("HTTP/1.0 200 Connection established".getBytes());
//            os.write("\r\n\r\n".getBytes());
//            this.threadPool.execute(new Piper(parser, out));
//
////            BufferedReader i = new BufferedReader(
////                    new InputStreamReader(in));
////            String line = null;
////            while ((line = i.readLine()) != null) {
////                Log.e("InputStream", line);
////            }
//
//
//            new Piper(in, os).run();
//            Log.e("paso", "OK");
//            parser.close();
//            os.close();
//
//        } catch (MalformedURLException e) {
//            Log.e(getClass().getName(), e.getMessage());
//        } catch (IOException e) {
//            Log.e(getClass().getName(), e.getMessage());
//            e.printStackTrace();
//        } finally {
//            if (proxySocket != null) {
//                try {
//                    proxySocket.close();
//                } catch (Exception fe) {
//                }
//            }
//            try {
//                os.close();
//            } catch (IOException e) {
//                Log.e(getClass().getName(), e.getMessage());
//            }
//            try {
//                parser.close();
//            } catch (IOException e) {
//                Log.e(getClass().getName(), e.getMessage());
//            }
//        }
//    }


    void doConnect(HttpParser parser, OutputStream os) {
        Log.i("making connection", parser.getUri());
        String[] uri = parser.getUri().split(":");
        ProxyClient client = new ProxyClient();
        client.getHostConfiguration().setHost(uri[0], Integer.parseInt(uri[1]));
        client.getHostConfiguration().setProxy(this.addr, this.inport);
        client.getState().setProxyCredentials(
                new AuthScope(AuthScope.ANY),
                credentials);
        Socket remoteSocket = null;
        try {
            ProxyClient.ConnectResponse response = client.connect();
            remoteSocket = response.getSocket();
            if (remoteSocket == null) {
                ConnectMethod method = response.getConnectMethod();
                os.write(method.getStatusLine().toString()
                        .getBytes());
                throw new IOException("Socket not created: "
                        + method.getStatusLine());
            }

            os.write(response.getConnectMethod().getStatusLine().toString()
                    .getBytes());

            os.write("\r\n\r\n".getBytes());


            this.threadPool.execute(new Piper(parser, remoteSocket
                    .getOutputStream()));

            new Piper(remoteSocket.getInputStream(), os).run();
            parser.close();
            os.close();
        } catch (Exception e) {
        } finally {
            if (remoteSocket != null) {
                try {
                    remoteSocket.close();
                } catch (Exception fe) {
                }
            }
            try {
                os.close();
            } catch (IOException e) {
            }
            try {
                parser.close();
            } catch (IOException e) {
            }
        }
    }

    class Handler implements Runnable {

        Socket localSocket;
        //ByteBuffer buffer = ByteBuffer.allocate(8192);

        public Handler(Socket localSocket) {
            this.localSocket = localSocket;
        }

        private boolean matches(String url, String bypass) {
            LinkedList<StringBuilder> patterns = new LinkedList<StringBuilder>();

            for (String i : bypass.split(",")) {
                StringBuilder s = new StringBuilder(i);
                if (i.length() > 0) {
                    while (s.charAt(0) == ' ') {
                        s.delete(0, 1);
                    }
                    if (s.charAt(0) == '*') {
                        s.insert(0, ' ');
                    }
                    patterns.add(s);
                }
            }

            for (StringBuilder i : patterns) {
                Pattern p = Pattern.compile(i.toString());
                if (p.matcher(url).find()) {
                    Log.i(getClass().getName(), "url matches bypass " + url);
                    return true;
                }
            }
            Log.i(getClass().getName(), "url does not matches bypass " + url);
            return false;
        }

        public void run() {
            try {
                HttpParser parser = new HttpParser(
                        this.localSocket.getInputStream());
                HttpMethod method = null;
                try {
                    while (!parser.parse()) {
                    }
                } catch (IOException e) {

                    parser.close();
                    return;
                }

                boolean matches = (bypass != null) && matches(parser.getUri().toString(), bypass);
                HttpClient client = matches ? HttpForwarder.this.noDelegateClient : HttpForwarder.this.delegateClient;

//                HttpClient client = HttpForwarder.this.delegateClient;

                if (parser.getMethod().equals("GET")) {
                    Log.i(getClass().getName(), "GET " + parser.getUri());
                    method = new GetMethod();
                } else if (parser.getMethod().equals("POST")) {
                    Log.i(getClass().getName(), "POST " + parser.getUri());
                    method = new PostMethod();
                } else if (parser.getMethod().equals("HEAD")) {
                    Log.i(getClass().getName(), "HEAD " + parser.getUri());
                    method = new HeadMethod();
                } else {
                    if (parser.getMethod().equals("CONNECT")) {
                        Log.i(getClass().getName(), "CONNECT " + parser.getUri());
                        if (!matches) {
                            HttpForwarder.this.doConnect(parser,
                                    this.localSocket.getOutputStream());
                        } else {
                            HttpForwarder.this.doConnectNoProxy(parser,
                                    this.localSocket.getOutputStream());
                        }
                        return;
                    }
                    throw new Exception("Unknown method: " + parser.getMethod());
                }
                if ((method instanceof EntityEnclosingMethod)) {
                    EntityEnclosingMethod method2 = (EntityEnclosingMethod) method;
                    method2.setRequestEntity(new StreamingRequestEntity(parser));
                }

                method.setURI(new URI(parser.getUri(), true));
                method.setFollowRedirects(false);
                method.getParams().setCookiePolicy("ignoreCookies");

                if (!matches) {
                    for (int i = 0; i < parser.getHeaders().length; i++) {
                        Header h = parser.getHeaders()[i];

                        if (HttpForwarder.stripHeadersIn.contains(h.getName())) {
                            continue;
                        }
                        method.addRequestHeader(h);
                    }
                }

                client.executeMethod(method);
                this.localSocket.shutdownInput();
                OutputStream os = this.localSocket.getOutputStream();
                os.write(method.getStatusLine().toString().getBytes());
                os.write("\r\n".getBytes());

                if (!matches) {
                    Header[] headers = method.getResponseHeaders();
                    for (int i = 0; i < headers.length; i++) {
                        if (HttpForwarder.stripHeadersOut.contains(headers[i])) {
                            continue;
                        }
                        os.write(headers[i].toExternalForm().getBytes());
                    }
                }

                InputStream is = method.getResponseBodyAsStream();

                if (is != null) {
                    os.write("\r\n".getBytes());
                    new Piper(is, os).run();
                }

                method.releaseConnection();
                this.localSocket.close();
            } catch (Exception e) {
                Log.e(getClass().getName(), e.getMessage());
            }
        }
    }

    public Socket GetSocket(String host, String port) throws IOException {

        /*************************
         * Get the jvm arguments
         *************************/

        int proxyPort = this.inport;
        String proxyHost = this.addr;

        // Socket object connecting to proxy
        Socket sock = new Socket(proxyHost, proxyPort);

        /***********************************
         * HTTP CONNECT protocol RFC 2616
         ***********************************/
        String proxyConnect = "CONNECT " + host + ":" + port;

        // Add Proxy Authorization if proxyUser and proxyPass is set
        try {
            String proxyUserPass = String.format("%s:%s",
                    this.user,
                    this.pass);

            proxyConnect.concat(" HTTP/1.0\nProxy-Authorization:Basic "
                    + Base64.encode(proxyUserPass.getBytes()));
        } catch (Exception e) {
            Log.e(getClass().getName(), e.getMessage());
        } finally {
            proxyConnect.concat("\n\n");
        }

        sock.getOutputStream().write(proxyConnect.getBytes());
        /***********************************/

        /***************************
         * validate HTTP response.
         ***************************/
        byte[] tmpBuffer = new byte[512];
        InputStream socketInput = sock.getInputStream();

        int len = socketInput.read(tmpBuffer, 0, tmpBuffer.length);

        if (len == 0) {
            throw new SocketException("Invalid response from proxy");
        }

        String proxyResponse = new String(tmpBuffer, 0, len, "UTF-8");

        // Expecting HTTP/1.x 200 OK
        if (proxyResponse.indexOf("200") != -1) {

            // Flush any outstanding message in buffer
            if (socketInput.available() > 0)
                socketInput.skip(socketInput.available());

            // Proxy Connect Successful, return the socket for IO
            return sock;
        } else {
            throw new SocketException("Fail to create Socket " + proxyResponse);
        }
    }

    /**
     * Simplest Base64 Encoder adopted from GeorgeK
     *
     */
    private static class Base64 {
        /***********************
         * Base64 character set
         ***********************/
        private final static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
                .toCharArray();

        /**
         * Translates the specified byte array into Base64 string.
         *
         * @param buf
         *            the byte array (not null)
         * @return the translated Base64 string (not null)
         */
        public static String encode(byte[] buf) {
            int size = buf.length;
            char[] ar = new char[((size + 2) / 3) * 4];
            int a = 0;
            int i = 0;
            while (i < size) {
                byte b0 = buf[i++];
                byte b1 = (i < size) ? buf[i++] : 0;
                byte b2 = (i < size) ? buf[i++] : 0;

                int mask = 0x3F;
                ar[a++] = ALPHABET[(b0 >> 2) & mask];
                ar[a++] = ALPHABET[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
                ar[a++] = ALPHABET[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
                ar[a++] = ALPHABET[b2 & mask];
            }
            switch (size % 3) {
                case 1:
                    ar[--a] = '=';
                case 2:
                    ar[--a] = '=';
            }
            return new String(ar);
        }
    }

}
