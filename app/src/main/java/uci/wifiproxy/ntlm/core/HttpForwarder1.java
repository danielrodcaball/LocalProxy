//package uci.ucintlm.ntlm.core;
//
//import android.util.Log;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.InetAddress;
//import java.net.MalformedURLException;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.net.URI;
//import java.net.UnknownHostException;
//import java.util.Arrays;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.regex.Pattern;
//
//import cz.msebera.android.httpclient.Header;
//import cz.msebera.android.httpclient.HttpConnection;
//import cz.msebera.android.httpclient.HttpEntityEnclosingRequest;
//import cz.msebera.android.httpclient.HttpException;
//import cz.msebera.android.httpclient.HttpHost;
//import cz.msebera.android.httpclient.auth.AuthScope;
//import cz.msebera.android.httpclient.auth.NTCredentials;
//import cz.msebera.android.httpclient.auth.UsernamePasswordCredentials;
//import cz.msebera.android.httpclient.client.CredentialsProvider;
//import cz.msebera.android.httpclient.client.HttpClient;
//import cz.msebera.android.httpclient.client.methods.HttpGet;
//import cz.msebera.android.httpclient.client.methods.HttpHead;
//import cz.msebera.android.httpclient.client.methods.HttpPost;
//import cz.msebera.android.httpclient.client.methods.HttpRequestBase;
//import cz.msebera.android.httpclient.client.params.AuthPolicy;
//import cz.msebera.android.httpclient.client.params.HttpClientParams;
//import cz.msebera.android.httpclient.impl.DefaultBHttpClientConnection;
//import cz.msebera.android.httpclient.impl.auth.DigestScheme;
//import cz.msebera.android.httpclient.impl.client.BasicCredentialsProvider;
//import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
//import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
//import cz.msebera.android.httpclient.impl.client.EntityEnclosingRequestWrapper;
//import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
//import cz.msebera.android.httpclient.impl.client.HttpClients;
//import cz.msebera.android.httpclient.impl.client.ProxyClient;
//import okhttp3.internal.http.HttpMethod;
////import uci.ucintlm.ntlm.ntlmv2.JCIFS_NTLMScheme;
//
///**
// * Created by daniel on 17/04/17.
// */
//
//public class HttpForwarder1 extends Thread {
//
//    static List<String> stripHeadersIn = Arrays.asList(new String[]{
//            "Content-Type", "Content-Length", "Proxy-Connection"});
//    static List<String> stripHeadersOut = Arrays.asList(new String[]{
//            "Proxy-Authentication", "Proxy-Authorization"});
//
//    public static final String NTLM_SCHEME = "NTLM";
//    public static final String BASIC_SCHEME = "Basic";
//    public static final String DIGEST_SCHEME = "Digest";
//    private ServerSocket ssocket;
//    private ExecutorService threadPool = Executors.newCachedThreadPool();
//    private CloseableHttpClient delegateClient;
//    private CloseableHttpClient noDelegateClient;
//    private int inport;
//    private String addr = "";
//    private String user;
//    private String pass;
//    private String domain;
//    private String bypass;
//    private String authScheme;
//    public boolean running = true;
//    private LinkedList<Socket> listaSockets = new LinkedList<Socket>();
//
//    private CredentialsProvider credentials = null;
//
//    public HttpForwarder1(String addr, int inport, String domain, String user,
//                          String pass, int outport, boolean onlyLocal, String bypass, String authScheme) throws IOException {
//        this.addr = addr;
//        this.inport = inport;
//        this.user = user;
//        this.pass = pass;
//        this.domain = domain;
//        this.bypass = bypass;
//        this.authScheme = authScheme;
//
//        if (onlyLocal) {
//            this.ssocket = new ServerSocket(outport, 0,
//                    InetAddress.getByName("127.0.0.1"));
//        } else {
//            this.ssocket = new ServerSocket(outport);
//        }
//
////        MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
////        manager.getParams().setDefaultMaxConnectionsPerHost(40);
//
//        credentials = new BasicCredentialsProvider();
//
//        if (authScheme.equals(NTLM_SCHEME)) {
//            credentials.setCredentials(
//                    new AuthScope(AuthScope.ANY),
//                    new NTCredentials(this.user, this.pass, InetAddress.getLocalHost()
//                            .getHostName(), this.domain));
//        } else if (authScheme.equals(BASIC_SCHEME)) {
//            credentials.setCredentials(
//                    new AuthScope(AuthScope.ANY),
//                    new UsernamePasswordCredentials(this.user, this.pass));
//        } else if (authScheme.equals(DIGEST_SCHEME)) {
//            credentials.setCredentials(
//                    new AuthScope(AuthScope.ANY),
//                    new UsernamePasswordCredentials(this.user, this.pass));
//        } else {
//            System.out.println("There is no authentication scheme selected");
//            return;
//        }
//
//        this.delegateClient = HttpClientBuilder.create().setProxy(new HttpHost(this.addr, this.inport))
//                .setDefaultCredentialsProvider(credentials)
//                .build();
//        this.noDelegateClient = HttpClientBuilder.create().build();
//
//        Log.e(getClass().getName(), "Starting proxy with " + authScheme + " scheme");
//    }
//
//    public void run() {
//        Log.e(getClass().getName(), "Proxy started");
//        while (running) {
//            try {
//                if (interrupted()) {
//                    Log.e(getClass().getName(), "The proxy task was interrupted");
//                }
//                Socket s = this.ssocket.accept();
//                listaSockets.add(s);
//                this.threadPool.execute(new HttpForwarder1.Handler(s));
//            } catch (IOException e) {
//                System.out.print(e.getMessage());
//            }
//        }
//    }
//
//    public void halt() {
//        Log.e(getClass().getName(), "Stoping proxy");
//        running = false;
//        terminate();
//    }
//
//    public void terminate() {
//        /*
//        *TODO: look for doc about java.util.ConcurrentModificationException
//        *this method crashes sometimes trying to access the list
//        * */
//        try {
//            for (Socket a : listaSockets) {
//                try {
//                    a.close();
//                } catch (Exception ex) {
//                    Log.e("Error closing socket", ex.getMessage());
//                }
//            }
//            listaSockets.clear();
//
//        } catch (java.util.ConcurrentModificationException concurrentModificationException) {
//            Log.e(HttpForwarder1.class.getName(), concurrentModificationException.getMessage());
//        } finally {
//            try {
//                this.close();
//            } catch (IOException e) {
//                Log.e(getClass().getName(), "Error closing server socket:" + e.getMessage());
//            }
//            this.running = false;
//        }
//    }
//
//    public void close() throws IOException {
//        this.ssocket.close();
//    }
//
//    void doConnectNoProxy(HttpParser parser, OutputStream os) {
//        Socket remoteSocket = null;
//        try {
//            Log.i("making connection", parser.getUri());
//            String[] uri = parser.getUri().split(":");
//            InputStream in = null;
//            OutputStream out = null;
//            try {
//                remoteSocket = new Socket(uri[0], Integer.parseInt(uri[1]));
//                in = remoteSocket.getInputStream();
//                out = remoteSocket.getOutputStream();
//            } catch (UnknownHostException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//            os.write("HTTP/1.1 200 Connection established".getBytes());
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
//            e.printStackTrace();
//        } catch (IOException e) {
//            Log.e("transport error", e.getMessage());
//            e.printStackTrace();
//        } finally {
//            if (remoteSocket != null) {
//                try {
//                    remoteSocket.close();
//                } catch (Exception fe) {
//                }
//            }
//            try {
//                os.close();
//            } catch (IOException e) {
//            }
//            try {
//                parser.close();
//            } catch (IOException e) {
//            }
//
//        }
//
//
//    }
//
//    void doConnect(HttpParser parser, OutputStream os) {
//        Log.i("making connection", parser.getUri());
//        String[] uri = parser.getUri().split(":");
//
//        ProxyClient client = new ProxyClient();
//        Socket remoteSocket = null;
//        HttpHost proxyHost = new HttpHost(this.addr, this.inport);
//        HttpHost targetHost = new HttpHost(uri[0], Integer.parseInt(uri[1]), "https");
//        try {
//            remoteSocket = client.tunnel(proxyHost, targetHost, credentials.getCredentials(AuthScope.ANY));
//            os.write("HTTP/1.1 200 Connection established".getBytes());
//            os.write("\r\n\r\n".getBytes());
//            this.threadPool.execute(new Piper(parser, remoteSocket
//                    .getOutputStream()));
//            new Piper(remoteSocket.getInputStream(), os).run();
//            parser.close();
//            os.close();
//        } catch (IOException e) {
//            Log.e(getClass().getName(), e.getMessage());
//        } catch (HttpException e1) {
//            Log.e(getClass().getName(), e1.getMessage());
//        } finally {
//            if (remoteSocket != null) {
//                try {
//                    remoteSocket.close();
//                } catch (Exception fe) {
//                }
//            }
//            try {
//                os.close();
//            } catch (IOException e) {
//            }
//            try {
//                parser.close();
//            } catch (IOException e) {
//            }
//        }
//    }
//
//    class Handler implements Runnable {
//
//        Socket localSocket;
//        //ByteBuffer buffer = ByteBuffer.allocate(8192);
//
//        public Handler(Socket localSocket) {
//            this.localSocket = localSocket;
//        }
//
//        private boolean matches(String url, String bypass) {
//            LinkedList<StringBuilder> patterns = new LinkedList<StringBuilder>();
//
//            for (String i : bypass.split(",")) {
//                StringBuilder s = new StringBuilder(i);
//                if (i.length() > 0) {
//                    while (s.charAt(0) == ' ') {
//                        s.delete(0, 1);
//                    }
//                    if (s.charAt(0) == '*') {
//                        s.insert(0, ' ');
//                    }
//                    patterns.add(s);
//                }
//            }
//
//            for (StringBuilder i : patterns) {
//                Pattern p = Pattern.compile(i.toString());
//                if (p.matcher(url).find()) {
//                    Log.i(getClass().getName(), "url matches bypass " + url);
//                    return true;
//                }
//            }
//            Log.i(getClass().getName(), "url does not matches bypass " + url);
//            return false;
//        }
//
//        public void run() {
//            try {
//                HttpParser parser = new HttpParser(
//                        this.localSocket.getInputStream());
//                HttpRequestBase method = null;
//                try {
//                    while (!parser.parse()) {
//                    }
//                } catch (IOException e) {
//
//                    parser.close();
//                    return;
//                }
//
//                boolean matches = (bypass != null) && matches(parser.getUri().toString(), bypass);
//                HttpClient client = matches ? HttpForwarder1.this.noDelegateClient : HttpForwarder1.this.delegateClient;
//
////                HttpClient client = HttpForwarder.this.delegateClient;
//
//                if (parser.getMethod().equals("GET")) {
//                    Log.i(getClass().getName(), "GET " + parser.getUri());
//                    method = new HttpGet();
//                } else if (parser.getMethod().equals("POST")) {
//                    Log.i(getClass().getName(), "POST " + parser.getUri());
//                    method = new HttpPost();
//                } else if (parser.getMethod().equals("HEAD")) {
//                    Log.i(getClass().getName(), "HEAD " + parser.getUri());
//                    method = new HttpHead();
//                } else {
//                    if (parser.getMethod().equals("CONNECT")) {
//                        Log.i(getClass().getName(), "CONNECT " + parser.getUri());
//                        if (!matches) {
//                            HttpForwarder1.this.doConnect(parser,
//                                    this.localSocket.getOutputStream());
//                        } else {
//                            HttpForwarder1.this.doConnectNoProxy(parser,
//                                    this.localSocket.getOutputStream());
//                        }
//                        return;
//                    }
//                    throw new Exception("Unknown method: " + parser.getMethod());
//                }
//                if ((method instanceof HttpEntityEnclosingRequest)) {
//                    HttpEntityEnclosingRequest method2 = (HttpEntityEnclosingRequest) method;
//                    method2.setEntity(new StreamingRequestEntity1(parser));
//                }
//
//                method.setURI(new URI(parser.getUri()));
//
////                if (!matches) {
////                    for (int i = 0; i < parser.getHeaders().length; i++) {
////                        Header h = parser.getHeaders()[i];
////
////                        if (HttpForwarder.stripHeadersIn.contains(h.getName())) {
////                            continue;
////                        }
////                        method.addRequestHeader(h);
////                    }
////                }
////
////                client.executeMethod(method);
////                this.localSocket.shutdownInput();
////                OutputStream os = this.localSocket.getOutputStream();
////                os.write(method.getStatusLine().toString().getBytes());
////                os.write("\r\n".getBytes());
////
////                if (!matches) {
////                    Header[] headers = method.getResponseHeaders();
////                    for (int i = 0; i < headers.length; i++) {
////                        if (HttpForwarder.stripHeadersOut.contains(headers[i])) {
////                            continue;
////                        }
////                        os.write(headers[i].toExternalForm().getBytes());
////                    }
////                }
////
////                InputStream is = method.getResponseBodyAsStream();
////
////                if (is != null) {
////                    os.write("\r\n".getBytes());
////                    new Piper(is, os).run();
////                }
//
//                method.releaseConnection();
//                this.localSocket.close();
//            } catch (Exception e) {
//            }
//        }
//    }
//
//}
