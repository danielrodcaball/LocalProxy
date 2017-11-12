package uci.wifiproxy.proxy.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uci.wifiproxy.R;
import uci.wifiproxy.util.WifiUtils;
//import uci.wifiproxy.ntlm.core.HttpForwarder;
import uci.wifiproxy.proxy.core.HttpForwarder1;
import uci.wifiproxy.proxy.ProxyActivity;


public class ProxyService extends Service {
    /*
     * Este es el servicio que inicia el servidor
     * Permanece en el área de notificación
     * */
    private String user = "";
    private String pass = "";
    private String server = "";
    private int inputport = 8080;
    private int outputport = 8080;
    private String bypass = "";
    private String firewallRules = "";

//    private ServerTask s;
    private HttpForwarder1 proxyThread;
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
        proxyThread.halt();
        executor.shutdown();
//        mNM.cancel(NOTIFICATION);

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && wifiAutoConfigReceiver != null){
//            unregisterReceiver(wifiAutoConfigReceiver);
//        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && set_global_proxy) {
            WifiUtils.unsetWifiProxySettings(this);
            Toast.makeText(this, getString(R.string.OnNoProxy), Toast.LENGTH_LONG).show();
        }

//        UCIntlmWidget.actualizarWidget(this.getApplicationContext(),
//                AppWidgetManager.getInstance(this.getApplicationContext()),
//                "off");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }

        if (intent.getExtras() == null) {
            Log.e(getClass().getName(), "Error starting service");
        }

        user = intent.getStringExtra("user");
        pass = intent.getStringExtra("pass");
        server = intent.getStringExtra("server");
        inputport = Integer.valueOf(intent.getStringExtra("inputport"));
        outputport = Integer.valueOf(intent.getStringExtra("outputport"));
        set_global_proxy = intent.getBooleanExtra("set_global_proxy", true);
        bypass = intent.getStringExtra("bypass");
        firewallRules = intent.getStringExtra("firewallRules");

        System.out.println("global_proxy: " + String.valueOf(set_global_proxy));
        if (set_global_proxy) {
            WifiUtils.setWifiProxySettings(this, outputport, "");
            Toast.makeText(this, getString(R.string.OnProxy), Toast.LENGTH_LONG).show();
        }

        Log.i(getClass().getName(), "Starting for user " + user  + ", server " + server + ", input port " + String.valueOf(inputport) + ", output port" + String.valueOf(outputport) + " and bypass string: " + bypass);

//        s = new ServerTask(user, pass, domain, server, inputport, outputport, bypass, authScheme);
//        s.execute();
        try {
            proxyThread = new HttpForwarder1(server, inputport, user, pass, outputport, true, bypass,
                    firewallRules, getPackageManager());
        } catch (IOException e) {
            Log.e(getClass().getName(), "The proxy thread can not be started: "  + e.getMessage());
            return START_NOT_STICKY;
        }

        executor.execute(proxyThread);
        notifyit();

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
                .setContentText(getApplicationContext().getString(R.string.notif2) + " " + user)
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
}
