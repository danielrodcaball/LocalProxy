package uci.localproxy.proxycore;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Proxy;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;


import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.WifiProxyChanger;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.exceptions.ApiNotSupportedException;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.exceptions.NullWifiConfigurationException;
import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import uci.localproxy.R;
import uci.localproxy.proxycore.core.HttpForwarder;
import uci.localproxy.proxyscreen.ProxyActivity;


public class ProxyService extends Service {
    /*
     * Este es el servicio que inicia el servidor
     * Permanece en el área de notificación
     * */

    public static final String MESSAGE_TAG = "message";

    public static final String SERVICE_RECIVER_NAME = "service-receiver";

    public static final int SERVICE_STARTED_SUCCESSFUL = 0;

    public static final int ERROR_STARTING_SERVICE = 1;

    public static boolean IS_SERVICE_RUNNING = false;

    private String user = "";

    //    private ServerTask s;
    private HttpForwarder proxyThread;
    private boolean set_global_proxy;

    private int NOTIFICATION = 1337;

    private ExecutorService executor;

    @Override
    public void onCreate() {
        executor = Executors.newSingleThreadExecutor();
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (proxyThread != null) {
            proxyThread.halt();
            executor.shutdown();
            if (set_global_proxy) {
                Toast.makeText(this, getString(R.string.OnNoProxy), Toast.LENGTH_LONG).show();
                try {
                    WifiProxyChanger.clearProxySettings(this);
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | InvocationTargetException |
                        NoSuchFieldException | IllegalAccessException | NullWifiConfigurationException | ApiNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }

        IS_SERVICE_RUNNING = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getExtras() == null) {
            Log.e(getClass().getName(), "Error starting service");
        }

        user = intent.getStringExtra("user");
        String pass = intent.getStringExtra("pass");
        String server = intent.getStringExtra("server");
        int inputport = Integer.valueOf(intent.getStringExtra("inputport"));
        int outputport = Integer.valueOf(intent.getStringExtra("outputport"));
        set_global_proxy = intent.getBooleanExtra("set_global_proxy", true);
        String bypass = intent.getStringExtra("bypass");
        String domain = intent.getStringExtra("domain");


        Log.i(getClass().getName(), "Starting for user " + user + ", server " + server + ", input port " + String.valueOf(inputport) + ", output port" + String.valueOf(outputport) + " and bypass string: " + bypass);

        try {
            proxyThread = new HttpForwarder(server, inputport, user, pass, outputport, true, bypass,
                    domain, getApplicationContext());

            executor.execute(proxyThread);
            IS_SERVICE_RUNNING = true;
            notifyit();

            //configuring wifi settings
            try {
                if (set_global_proxy) {
                    Toast.makeText(this, getString(R.string.OnProxy), Toast.LENGTH_LONG).show();
                    WifiProxyChanger.changeWifiStaticProxySettings("127.0.0.1", outputport, this);
                }
            }catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | InvocationTargetException |
                    NoSuchFieldException | IllegalAccessException | NullWifiConfigurationException | ApiNotSupportedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
//            Intent i = new Intent(SERVICE_RECIVER_NAME);
//            i.putExtra(MESSAGE_TAG, ERROR_STARTING_SERVICE);
//            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        }

        //START_REDELIVER_INTENT permite que si el sistema mata el servicio entonces cuando intenta reiniciarlo envia el mismo Intent que se envio para
        //iniciarlo por primera vez
        return START_REDELIVER_INTENT;
    }

    public void notifyit() {
        /*
         * Este método asegura que el servicio permanece en el área de notificación
		 * */

        Intent i = new Intent(this, ProxyActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);


        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getApplicationContext().getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(getApplicationContext().getString(R.string.excuting_proxy_service_notification) + " " + user)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent);


        Notification notification;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.getNotification();
        } else {
            notification = builder.build();
            notification.priority = Notification.PRIORITY_MAX;
        }

        notification.flags |= Notification.FLAG_NO_CLEAR;

        startForeground(NOTIFICATION, notification);
    }


    //This from <SandroProxy proyect>/projects/SandroProxyPlugin/src/org/sandroproxy/plugin/gui/MainActivity.java
//    private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ProxyService.class.getName());
    private void ipTablesForTransparentProxy(boolean activate) {
        int processId = getApplicationInfo().uid;
        String excludedUid = String.valueOf(processId);
        String action = (activate) ? "A" : "D";
        String chainName = "spplugin";
        String chainName1 = "sppluginOutput";
        List<String> rules = new ArrayList<String>();

        String r = "iptables -t nat -" + action + " OUTPUT -p 6 -d 10.0.0.1 -j RETURN";
        String redirectRule = "iptables -t nat -" + action + " OUTPUT -p 6 --dport 80 -m owner ! --uid-owner " + excludedUid + " -j REDIRECT --to-port 8080 ";
        String redirectRule2 = "iptables -t nat -" + action + " OUTPUT -p 6 --dport 443 -m owner ! --uid-owner " + excludedUid + " -j REDIRECT --to-port 8080 ";
        String redirectRule3 = "iptables -t nat -" + action + " OUTPUT -p 6 --dport 5228 -m owner ! --uid-owner " + excludedUid + " -j REDIRECT --to-port 8080 ";

        try {
            Command command0 = new Command(4, r) {
                @Override
                public void commandOutput(int id, String line) {
                    super.commandOutput(id, line);
                    Log.e("command output", line);
                }
            };
            Command command = new Command(0, redirectRule);
            Command command1 = new Command(1, redirectRule2);
            Command command2 = new Command(1, redirectRule3);
            Shell shell = RootTools.getShell(true);
            shell.add(command0);
            shell.add(command);
            shell.add(command1);
            shell.add(command2);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (RootDeniedException e) {
            e.printStackTrace();
        }
//        String redirectRule2 = "iptables -t nat -" + action + " OUTPUT -p 6 --dport 0:65535 -m owner ! --uid-owner " + excludedUid + " -j REDIRECT --to-port 8080 ";
//        rules.add(redirectRule2);

//        String rule0 = "iptables -" + action + " POSTROUTING -t nat -j MASQUERADE";
//        String rule1 = "iptables -t nat -" + action + " OUTPUT -m owner --uid-owner " + excludedUid  + " -j ACCEPT";
//        String rule2 = "iptables -t nat -" + action + " OUTPUT -p tcp --dport 80 -j REDIRECT --to-port 8080";
//        String rule3 = "iptables -t nat -" + action + " OUTPUT -p tcp --dport 443 -j REDIRECT --to-port 8080";
////        String rule3 = "iptables -t nat -" + action + " OUTPUT -p tcp --dport 80 -j DNAT --to :8080";
//        rules.add(rule0);
//        rules.add(rule1);
//        rules.add(rule2);
//        rules.add(rule3);


//        if (activate){
//            action = "A";
//            String createChainRule = "iptables --new " + chainName; rules.add(createChainRule);
//            String createNatChainRule = "iptables -t nat --new " + chainName; rules.add(createNatChainRule);
//            String createNatChainRule1 = "iptables -t nat --new " + chainName1; rules.add(createNatChainRule1);
//        }else{
//            action = "D";
//            String dettachChainRule = "iptables -D INPUT -j " + chainName; rules.add(dettachChainRule);
//            String dettachNatChainRule = "iptables -t nat -D PREROUTING -j " + chainName; rules.add(dettachNatChainRule);
//            String dettachNatChainRule1 = "iptables -t nat -D OUTPUT -j " + chainName1; rules.add(dettachNatChainRule1);
//        }
//

//        Process p;
//        try {
//            p = Runtime.getRuntime().exec(new String[]{"su", "-c", "sh"});
//
//            DataOutputStream stdin = new DataOutputStream(p.getOutputStream());
//            DataInputStream stdout = new DataInputStream(p.getInputStream());
//            InputStream stderr = p.getErrorStream();
//
//            for (String rule : rules) {
////                logger.finest(rule);
//                stdin.writeBytes(rule + "\n");
//                stdin.writeBytes("echo $?\n");
//                Thread.sleep(100);
//                byte[] buffer = new byte[4096];
//                int read = 0;
//                String out = new String();
//                String err = new String();
//                while (true) {
//                    read = stdout.read(buffer);
//                    out += new String(buffer, 0, read);
//                    if (read < 4096) {
//                        break;
//                    }
//                }
//                while (stderr.available() > 0) {
//                    read = stderr.read(buffer);
//                    err += new String(buffer, 0, read);
//                    if (read < 4096) {
//                        break;
//                    }
//                }
//                if (out != null && out.trim().length() > 0) Log.d(getClass().getName(), out);
//                if (err != null && err.trim().length() > 0) Log.d(getClass().getName(), err);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
////            logger.finest("Error executing rules: " + e.getMessage());
//        }
    }

    private static boolean setLollipopWebViewProxy(Context appContext, String host, int port) {
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", port + "");
        System.setProperty("https.proxyHost", host);
        System.setProperty("https.proxyPort", port + "");
        try {
            Class applictionCls = Class.forName("android.app.Application");
            Field loadedApkField = applictionCls.getDeclaredField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(appContext);
            Class loadedApkCls = Class.forName("android.app.LoadedApk");
            Field receiversField = loadedApkCls.getDeclaredField("mReceivers");
            receiversField.setAccessible(true);
            ArrayMap receivers = (ArrayMap) receiversField.get(loadedApk);
            for (Object receiverMap : receivers.values()) {
                for (Object rec : ((ArrayMap) receiverMap).keySet()) {
                    Class clazz = rec.getClass();
                    if (clazz.getName().contains("ProxyChangeListener")) {
                        Method onReceiveMethod = clazz.getDeclaredMethod("onReceive", Context.class, Intent.class);
                        Intent intent = new Intent(Proxy.PROXY_CHANGE_ACTION);
                        /***** In Lollipop, ProxyProperties went public as ProxyInfo *****/
                        final String CLASS_NAME = "android.net.ProxyInfo";
                        Class cls = Class.forName(CLASS_NAME);
                        /***** ProxyInfo lacks constructors, use the static buildDirectProxy method instead *****/
                        Method buildDirectProxyMethod = cls.getMethod("buildDirectProxy", String.class, Integer.TYPE);
                        Object proxyInfo = buildDirectProxyMethod.invoke(cls, host, port);
                        intent.putExtra("proxy", (Parcelable) proxyInfo);
                        onReceiveMethod.invoke(rec, appContext, intent);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Setting proxy error", "Setting proxy with >= 5.0 API failed with", e);
            return false;
        }
        Log.d("Setting proxy success", "Setting proxy with >= 5.0 API successful!");
        return true;
    }

}
