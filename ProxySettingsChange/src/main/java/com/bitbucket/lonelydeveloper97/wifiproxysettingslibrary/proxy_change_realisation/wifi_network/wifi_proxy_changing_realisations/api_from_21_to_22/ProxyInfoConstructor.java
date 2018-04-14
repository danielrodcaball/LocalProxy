package com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.wifi_proxy_changing_realisations.api_from_21_to_22;

import android.net.ProxyInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public abstract class ProxyInfoConstructor {

    public static ProxyInfo proxyInfo(String host, int port)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        return proxyInfo(host, port, null);
    }

    public static ProxyInfo proxyInfo(String host, int port, String exclude)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Object newProxyInfo = proxyInfoConstructor().newInstance(host, port, exclude);
        return (ProxyInfo) newProxyInfo;

    }

    private static Constructor proxyInfoConstructor()
            throws ClassNotFoundException, NoSuchMethodException {
        return Class.forName("android.net.ProxyInfo").getConstructor(String.class, int.class, String.class);
    }

}
