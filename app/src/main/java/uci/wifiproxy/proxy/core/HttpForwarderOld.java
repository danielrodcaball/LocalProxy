//package uci.wifiproxy.proxy.core;
//
//import android.util.Log;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.InetAddress;
//import java.net.ServerSocket;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import cz.msebera.android.httpclient.Header;
//import cz.msebera.android.httpclient.HttpEntityEnclosingRequest;
//import cz.msebera.android.httpclient.HttpHost;
//import cz.msebera.android.httpclient.HttpResponse;
//import cz.msebera.android.httpclient.auth.AuthScope;
//import cz.msebera.android.httpclient.auth.NTCredentials;
//import cz.msebera.android.httpclient.client.CredentialsProvider;
//import cz.msebera.android.httpclient.client.HttpClient;
//import cz.msebera.android.httpclient.client.methods.HttpDelete;
//import cz.msebera.android.httpclient.client.methods.HttpGet;
//import cz.msebera.android.httpclient.client.methods.HttpHead;
//import cz.msebera.android.httpclient.client.methods.HttpOptions;
//import cz.msebera.android.httpclient.client.methods.HttpPost;
//import cz.msebera.android.httpclient.client.methods.HttpPut;
//import cz.msebera.android.httpclient.client.methods.HttpTrace;
//import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
//import cz.msebera.android.httpclient.client.methods.RequestBuilder;
//import cz.msebera.android.httpclient.impl.client.BasicCredentialsProvider;
//import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
//import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
//import cz.msebera.android.httpclient.impl.client.ProxyClient;
//import cz.msebera.android.httpclient.impl.conn.PoolingHttpClientConnectionManager;
//import uci.wifiproxy.firewall.Firewall;
//import uci.wifiproxy.util.StringUtils;
//
///**
// * Created by daniel on 17/04/17.
// */
//
//public class HttpForwarderOld extends Thread {
//
//    private static List<String> stripHeadersIn = Arrays.asList(
//            "Content-Type", "Content-Length", "Proxy-Connection"
//    );
//    private static List<String> stripHeadersOut = Arrays.asList(
//            "Proxy-Authentication", "Proxy-Authorization", "Transfer-Encoding"
//    );
//
//    private ServerSocket ssocket;
//    private PoolingHttpClientConnectionManager manager;
//    private ExecutorService threadPool = Executors.newCachedThreadPool();
//    private CloseableHttpClient delegateClient;
//    private CloseableHttpClient noDelegateClient;
//
//    private int inport;
//    private String addr = "";
//    private String user;
//    private String pass;
//    private String bypass;
//
//    public boolean running = true;
//
//    private CredentialsProvider credentials = null;
//
//    private Firewall firewall;
//
//
//    public HttpForwarderOld(String addr, int inport, String user,
//                            String pass, int outport, boolean onlyLocal, String bypass) throws IOException {
//        this.addr = addr;
//        this.inport = inport;
//        this.user = user;
//        this.pass = pass;
//        this.bypass = bypass;
//
//        if (onlyLocal) {
//            this.ssocket = new ServerSocket(outport, 0,
//                    InetAddress.getByName("127.0.0.1"));
//        } else {
//            this.ssocket = new ServerSocket(outport);
//        }
//
//        manager = new PoolingHttpClientConnectionManager();
//        manager.setDefaultMaxPerRoute(20);
//        manager.setMaxTotal(200);
//
//        credentials = new BasicCredentialsProvider();
//
//
//        Log.e(getClass().getName(), "Starting proxy");
//    }
//
//    public void setFirewall(Firewall firewall) {
//        this.firewall = firewall;
//    }
//
//    public void run() {
//        try {
//            //NTCredentials extends from UsernamePasswordCredential which means that can resolve
//            //Basic, Digest and NTLM authentication schemes. The field of domain act like an realm,
//            //it can be null and it will works correctly
//            credentials.setCredentials(new AuthScope(AuthScope.ANY),
//                    new NTCredentials(this.user, this.pass, InetAddress.getLocalHost().getHostName(),
//                            null));
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//
//        this.delegateClient = HttpClientBuilder.create()
//                .setConnectionManager(manager)
//                .setProxy(new HttpHost(this.addr, this.inport))
//                .setDefaultCredentialsProvider(credentials)
//                .disableRedirectHandling()
//                .disableCookieManagement()
//                .disableAuthCaching()
//                .build();
//
//        this.noDelegateClient = HttpClientBuilder.create()
//                .setConnectionManager(manager)
//                .disableRedirectHandling()
//                .disableCookieManagement()
//                .build();
//
//        while (running) {
//            try {
////                if (interrupted()) {
////                    Log.e(getClass().getName(), "The proxy task was interrupted");
////                }
//                Socket s = this.ssocket.accept();
//                this.threadPool.execute(new Handler(s));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void halt() {
//        Log.e(getClass().getName(), "Stoping proxy");
//        running = false;
////        terminate();
//        try {
//            this.delegateClient.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            this.noDelegateClient.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        manager.shutdown();
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
//            remoteSocket = new Socket(uri[0], Integer.parseInt(uri[1]));
//            in = remoteSocket.getInputStream();
//            out = remoteSocket.getOutputStream();
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
//            new Piper(in, os).run();
//            Log.e("paso", "OK");
//            parser.close();
//            os.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (remoteSocket != null) {
//                try {
//                    remoteSocket.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            try {
//                os.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                parser.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    void doConnect(HttpParser parser, OutputStream os) {
//        Log.i("making connection", parser.getUri());
//        String[] uri = parser.getUri().split(":");
//
//        ProxyClient client = new ProxyClient();
//        Socket remoteSocket = null;
//        HttpHost proxyHost = new HttpHost(this.addr, this.inport);
//        HttpHost targetHost = new HttpHost(uri[0], Integer.parseInt(uri[1]));
//        try {
//            remoteSocket = client.tunnel(proxyHost, targetHost, credentials.getCredentials(AuthScope.ANY));
//            os.write("HTTP/1.1 200 Connection established".getBytes());
//            os.write("\r\n\r\n".getBytes());
//            this.threadPool.execute(new Piper(parser, remoteSocket
//                    .getOutputStream()));
//            new Piper(remoteSocket.getInputStream(), os).run();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (remoteSocket != null) {
//                try {
//                    remoteSocket.close();
//                } catch (Exception fe) {
//                    fe.printStackTrace();
//                }
//            }
//            try {
//                os.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                parser.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
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
//
//        public void run() {
//            try {
//                HttpParser parser = new HttpParser(
//                        this.localSocket.getInputStream());
//                try {
//                    while (!parser.parse()) {
//                    }
//                } catch (IOException e) {
//                    parser.close();
//                    return;
//                }
//
////                Log.e("Socket", this.localSocket.getPort() + "");
//
//                if (firewall != null && !firewall.filter(localSocket.getPort(),
//                        localSocket.getInetAddress().getHostAddress(),
//                        parser.getUri()))
//                {
//                    OutputStream os = localSocket.getOutputStream();
//                    os.write("HTTP/1.1 403 Forbidden".getBytes());
//                    os.write("\r\n".getBytes());
//                    os.write("\r\n".getBytes());
//                    os.write("<h1>Forbidden by the local Firewall</h1>".getBytes());
//                    return;
//                }
//
//                boolean matches = (bypass != null) && StringUtils.matches(parser.getUri(), bypass);
//                HttpClient client;
//                if (matches) {
//                    client = HttpForwarderOld.this.noDelegateClient;
//                    Log.i(getClass().getName(), "url matches bypass " + parser.getUri());
//                } else {
//                    client = HttpForwarderOld.this.delegateClient;
//                    Log.i(getClass().getName(), "url does not matches bypass " + parser.getUri());
//                }
//
//                if (parser.getMethod().equals("CONNECT")) {
//                    Log.i(getClass().getName(), "CONNECT " + parser.getUri());
//                    if (!matches) {
//                        HttpForwarderOld.this.doConnect(parser, this.localSocket.getOutputStream());
//                    } else {
//                        HttpForwarderOld.this.doConnectNoProxy(parser, this.localSocket.getOutputStream());
//                    }
//                    return;
//                } else {
//                    HttpUriRequest request;
//                    Log.i(getClass().getName(), parser.getMethod() + " " + parser.getUri());
//                    if (parser.getMethod().equals("GET")) {
//                        request = new HttpGet(parser.getUri());
//                    } else if (parser.getMethod().equals("POST")) {
//                        request = new HttpPost(parser.getUri());
//                    } else if (parser.getMethod().equals("HEAD")) {
//                        request = new HttpHead(parser.getUri());
//                    } else if (parser.getMethod().equals("PUT")) {
//                        request = new HttpPut(parser.getUri());
//                    } else if (parser.getMethod().equals("DELETE")) {
//                        request = new HttpDelete(parser.getUri());
//                    } else if (parser.getMethod().equals("OPTIONS")) {
//                        request = new HttpOptions(parser.getUri());
//                    } else if (parser.getMethod().equals("TRACE")) {
//                        request = new HttpTrace(parser.getUri());
//                    } else {
//                        request = RequestBuilder.create(parser.getMethod())
//                                .setUri(parser.getUri())
//                                .build();
//                    }
//
//                    if (request instanceof HttpEntityEnclosingRequest) {
//                        HttpEntityEnclosingRequest request1 = (HttpEntityEnclosingRequest) request;
//                        request1.setEntity(new StreamingRequestEntity(parser));
//                    }
//
//                    Header[] headers = parser.getHeaders();
////                            if (!matches) {
//                    for (int i = 0; i < headers.length; i++) {
//                        Header h = headers[i];
////                            Log.i("HEADER_REQUEST", h.toString());
//                        if (stripHeadersIn.contains(h.getName())) continue;
//                        request.addHeader(h);
//                    }
////                            }
//
//                    HttpResponse response = client.execute(request);
//                    localSocket.shutdownInput();
//                    OutputStream os = localSocket.getOutputStream();
//
////                            String line;
////                            BufferedReader bf = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
////                            while ((line = bf.readLine()) != null) {
////                                Log.e("InputStream", line);
////                            }
//
//                    os.write(response.getStatusLine().toString().getBytes());
//                    Log.e("STATUS-LINE", response.getStatusLine().toString());
//                    os.write("\r\n".getBytes());
//
////                            if (!matches) {
//                    headers = response.getAllHeaders();
//                    for (int i = 0; i < headers.length; i++) {
//                        Header h = headers[i];
////                            Log.i("HEADER_RESPONSE", h.toString());
//                        if (stripHeadersOut.contains(h.getName())) continue;
//                        os.write((h.toString() + "\r\n").getBytes());
//                    }
////                            }
//
//                    os.write("\r\n".getBytes());
//
//                    if (response.getEntity() != null) {
//                        new Piper(response.getEntity().getContent(), os).run();
//                    }
//                }
////                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    this.localSocket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//}
