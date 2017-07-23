package uci.wifiproxy.ntlm.core;

import android.util.Log;

import org.apache.commons.httpclient.methods.InputStreamRequestEntity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpEntityEnclosingRequest;
import cz.msebera.android.httpclient.HttpException;
import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.HttpRequest;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.auth.AUTH;
import cz.msebera.android.httpclient.auth.AuthScope;
import cz.msebera.android.httpclient.auth.NTCredentials;
import cz.msebera.android.httpclient.auth.UsernamePasswordCredentials;
import cz.msebera.android.httpclient.client.AuthenticationStrategy;
import cz.msebera.android.httpclient.client.CredentialsProvider;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpHead;
import cz.msebera.android.httpclient.client.methods.HttpOptions;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.client.methods.HttpRequestBase;
import cz.msebera.android.httpclient.client.methods.HttpTrace;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.client.methods.RequestBuilder;
import cz.msebera.android.httpclient.client.params.AuthPolicy;
import cz.msebera.android.httpclient.client.protocol.HttpClientContext;
import cz.msebera.android.httpclient.config.ConnectionConfig;
import cz.msebera.android.httpclient.entity.InputStreamEntity;
import cz.msebera.android.httpclient.impl.client.BasicCredentialsProvider;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.impl.client.ProxyClient;
import cz.msebera.android.httpclient.impl.conn.PoolingHttpClientConnectionManager;
import cz.msebera.android.httpclient.impl.execchain.TunnelRefusedException;
import cz.msebera.android.httpclient.impl.io.DefaultHttpRequestParser;
import cz.msebera.android.httpclient.impl.io.HttpRequestParser;
import cz.msebera.android.httpclient.io.HttpMessageParser;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by daniel on 17/04/17.
 */

public class HttpForwarder1 extends Thread {

    static List<String> stripHeadersIn = Arrays.asList(new String[]{
            "Content-Type", "Content-Length", "Proxy-Connection"});
    static List<String> stripHeadersOut = Arrays.asList(new String[]{
            "Proxy-Authentication", "Proxy-Authorization", "Transfer-Encoding"});


    public static final String NTLM_SCHEME = "NTLM";
    public static final String BASIC_SCHEME = "Basic";
    public static final String DIGEST_SCHEME = "Digest";
    private ServerSocket ssocket;
    PoolingHttpClientConnectionManager manager;
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    //    private ExecutorService threadPool = Executors.newSingleThreadExecutor();
    private CloseableHttpClient delegateClient;
    private CloseableHttpClient noDelegateClient;
    private int inport;
    private String addr = "";
    private String user;
    private String pass;
    private String domain;
    private String bypass;
    private String authScheme;
    public boolean running = true;
    private LinkedList<Socket> listaSockets = new LinkedList<Socket>();

    private CredentialsProvider credentials = null;

    public HttpForwarder1(String addr, int inport, String domain, String user,
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

        manager = new PoolingHttpClientConnectionManager();
        manager.setDefaultMaxPerRoute(20);
        manager.setMaxTotal(200);

        credentials = new BasicCredentialsProvider();


        Log.e(getClass().getName(), "Starting proxy with " + authScheme + " scheme");
    }

    public void run() {
        Log.e(getClass().getName(), "Proxy started");
        if (authScheme.equals(NTLM_SCHEME)) {
            try {
                credentials.setCredentials(
                        new AuthScope(AuthScope.ANY),
                        new NTCredentials(this.user, this.pass, InetAddress.getLocalHost()
                                .getHostName(), this.domain));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        } else if (authScheme.equals(BASIC_SCHEME)) {
            credentials.setCredentials(
                    new AuthScope(AuthScope.ANY),
                    new UsernamePasswordCredentials(this.user, this.pass));
        } else if (authScheme.equals(DIGEST_SCHEME)) {
            credentials.setCredentials(
                    new AuthScope(AuthScope.ANY),
                    new UsernamePasswordCredentials(this.user, this.pass));
        } else {
            Log.e(getClass().getName(), "There is no authentication scheme selected");
            return;
        }

        this.delegateClient = HttpClientBuilder.create()
                .setConnectionManager(manager)
                .setProxy(new HttpHost(this.addr, this.inport))
                .setDefaultCredentialsProvider(credentials)
                .disableRedirectHandling()
                .disableCookieManagement()
                .build();

        this.noDelegateClient = HttpClientBuilder.create()
                .setConnectionManager(manager)
                .disableRedirectHandling()
                .build();

        while (running) {
            try {
//                if (interrupted()) {
//                    Log.e(getClass().getName(), "The proxy task was interrupted");
//                }
                Socket s = this.ssocket.accept();
                listaSockets.add(s);
                this.threadPool.execute(new HttpForwarder1.Handler(s));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void halt() {
        Log.e(getClass().getName(), "Stoping proxy");
        running = false;
        terminate();

        manager.shutdown();
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
                    ex.printStackTrace();
                }
            }
            listaSockets.clear();

        } catch (java.util.ConcurrentModificationException concurrentModificationException) {
            concurrentModificationException.printStackTrace();
        } finally {
            try {
                this.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.running = false;
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
            remoteSocket = new Socket(uri[0], Integer.parseInt(uri[1]));
            in = remoteSocket.getInputStream();
            out = remoteSocket.getOutputStream();

            os.write("HTTP/1.1 200 Connection established".getBytes());
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

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (remoteSocket != null) {
                try {
                    remoteSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                parser.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    void doConnect(HttpParser parser, OutputStream os) {
        Log.i("making connection", parser.getUri());
        String[] uri = parser.getUri().split(":");

        ProxyClient client = new ProxyClient();
        Socket remoteSocket = null;
        HttpHost proxyHost = new HttpHost(this.addr, this.inport);
        HttpHost targetHost = new HttpHost(uri[0], Integer.parseInt(uri[1]));
        try {
            remoteSocket = client.tunnel(proxyHost, targetHost, credentials.getCredentials(AuthScope.ANY));
            os.write("HTTP/1.1 200 Connection established".getBytes());
            os.write("\r\n\r\n".getBytes());
            this.threadPool.execute(new Piper(parser, remoteSocket
                    .getOutputStream()));
            new Piper(remoteSocket.getInputStream(), os).run();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (remoteSocket != null) {
                try {
                    remoteSocket.close();
                } catch (Exception fe) {
                    fe.printStackTrace();
                }
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                parser.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void resolveNoProxy(HttpParser parser, OutputStream os) {
        Socket remoteSocket = null;
        try {
            Log.i("making connection", parser.getUri());
            URL url = new URL(parser.getUri());
            InputStream in = null;
            OutputStream out = null;
            remoteSocket = new Socket(url.getHost(), (url.getPort() == -1) ? 80 : url.getPort());
            os.write("HTTP/1.1 200 OK".getBytes());
            os.write("\r\n".getBytes());
            in = remoteSocket.getInputStream();
            out = remoteSocket.getOutputStream();
            InputStream is = new ByteArrayInputStream(parser.buffer);
            threadPool.execute(new Piper(is, out));
//                            BufferedReader i = new BufferedReader(
//                                    new InputStreamReader(in));
//                            String line = null;
//                            while ((line = i.readLine()) != null) {
//                                Log.e("InputStream", line);
//                            }
            new Piper(in, os).run();
            Log.e("paso", "OK");
            parser.close();
            os.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            for (StackTraceElement s : e.getStackTrace()) {
                e.printStackTrace();
            }
        } finally {
            if (remoteSocket != null) {
                try {
                    remoteSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                parser.close();
            } catch (IOException e) {
                e.printStackTrace();
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
                try {
                    while (!parser.parse()) {
                    }
                } catch (IOException e) {
                    parser.close();
                    return;
                }

                boolean matches = (bypass != null) && matches(parser.getUri().toString(), bypass);
                HttpClient client = matches ? HttpForwarder1.this.noDelegateClient : HttpForwarder1.this.delegateClient;

                if (parser.getMethod().equals("CONNECT")) {
                    Log.i(getClass().getName(), "CONNECT " + parser.getUri());
                    if (!matches) {
                        HttpForwarder1.this.doConnect(parser, this.localSocket.getOutputStream());
                    } else {
                        HttpForwarder1.this.doConnectNoProxy(parser, this.localSocket.getOutputStream());
                    }
                    return;
                } else {
                    HttpUriRequest request;
                    Log.i(getClass().getName(), parser.getMethod() + " " + parser.getUri());
                    if (parser.getMethod().equals("GET")) {
                        request = new HttpGet(parser.getUri());
                    } else if (parser.getMethod().equals("POST")) {
                        request = new HttpPost(parser.getUri());
                    } else if (parser.getMethod().equals("HEAD")) {
                        request = new HttpHead(parser.getUri());
                    } else if (parser.getMethod().equals("PUT")) {
                        request = new HttpPut(parser.getUri());
                    } else if (parser.getMethod().equals("DELETE")) {
                        request = new HttpDelete(parser.getUri());
                    } else if (parser.getMethod().equals("OPTIONS")) {
                        request = new HttpOptions(parser.getUri());
                    } else if (parser.getMethod().equals("TRACE")) {
                        request = new HttpTrace(parser.getUri());
                    } else {
                        request = RequestBuilder.create(parser.getMethod())
                                .setUri(parser.getUri())
                                .build();
                    }

                    //TODO: test this statement
                    if (request instanceof HttpEntityEnclosingRequest) {
                        HttpEntityEnclosingRequest request1 = (HttpEntityEnclosingRequest) request;
                        request1.setEntity(new StreamingRequestEntity(parser));
                    }

                    Header[] headers = parser.getHeaders();
//                            if (!matches) {
                    for (int i = 0; i < headers.length; i++) {
                        Header h = headers[i];
//                            Log.i("HEADER_REQUEST", h.toString());
                        if (stripHeadersIn.contains(h.getName())) continue;
                        request.addHeader(h);
                    }
//                            }

                    HttpResponse response = client.execute(request);
                    localSocket.shutdownInput();
                    OutputStream os = localSocket.getOutputStream();

//                            String line;
//                            BufferedReader bf = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//                            while ((line = bf.readLine()) != null) {
//                                Log.e("InputStream", line);
//                            }

                    os.write(response.getStatusLine().toString().getBytes());
                    Log.e("STATUS-LINE", response.getStatusLine().toString());
                    os.write("\r\n".getBytes());

//                            if (!matches) {
                    headers = response.getAllHeaders();
                    for (int i = 0; i < headers.length; i++) {
                        Header h = headers[i];
//                            Log.i("HEADER_RESPONSE", h.toString());
                        if (stripHeadersOut.contains(h.getName())) continue;
                        os.write((h.toString() + "\r\n").getBytes());
                    }
//                            }

                    os.write("\r\n".getBytes());

                    if (response.getEntity() != null) {
                        new Piper(response.getEntity().getContent(), os).run();
                    }

                    this.localSocket.close();
                }
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
