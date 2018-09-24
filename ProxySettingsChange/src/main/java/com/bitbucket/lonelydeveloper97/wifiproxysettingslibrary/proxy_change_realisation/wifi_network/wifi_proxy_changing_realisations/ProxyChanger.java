package com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.wifi_proxy_changing_realisations;


import android.net.wifi.WifiConfiguration;

import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.exceptions.ApiNotSupportedException;

import java.lang.reflect.InvocationTargetException;

public interface ProxyChanger {

    void setProxySettings(ProxySettings proxySettings)
            throws NoSuchFieldException, IllegalAccessException;

    ProxySettings getProxySettings()
            throws NoSuchFieldException, IllegalAccessException;

    void setProxyHostAndPort(String host, int port)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException, NoSuchFieldException;

    String getProxyHost()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            ApiNotSupportedException, NoSuchFieldException;

    int getProxyPort()
            throws ApiNotSupportedException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, NoSuchFieldException;

    boolean isProxySetted()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            ApiNotSupportedException, NoSuchFieldException;

    WifiConfiguration getWifiConfiguration();

}
