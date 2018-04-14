package com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network;

import android.os.Build;

public abstract class ApiChecker {

    public static boolean isJellyBeanOrKitkat() {
        return Build.VERSION.SDK_INT > 14 && Build.VERSION.SDK_INT < 20;
    }

    public static boolean isLolipop() {
        return Build.VERSION.SDK_INT > 20 && Build.VERSION.SDK_INT < 23;
    }

    public static boolean isSupportedApi() {
        return isJellyBeanOrKitkat() || isLolipop();
    }

}
