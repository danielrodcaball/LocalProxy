package com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;


public abstract class NetworkHelper {

    public static WifiManager getWifiManager(Context context) {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public static NetworkInfo getWifiNetworkInfo(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    public static NetworkInfo getMobileNetworkInfo(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    }

    public static boolean isWifiConnected(Context context) {
        return NetworkHelper.getWifiNetworkInfo(context).isConnected();
    }

}
