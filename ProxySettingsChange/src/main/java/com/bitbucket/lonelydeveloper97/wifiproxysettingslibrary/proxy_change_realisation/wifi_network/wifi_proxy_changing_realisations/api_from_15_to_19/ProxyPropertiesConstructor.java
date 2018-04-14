package com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.wifi_proxy_changing_realisations.api_from_15_to_19;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public abstract class ProxyPropertiesConstructor {

    public static Object proxyProperties(String host, int port)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        return proxyProperties(host, port, null);
    }

    public static Object proxyProperties(String host, int port, String exclList)
            throws NoSuchMethodException, ClassNotFoundException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        return proxyPropertiesConstructor().newInstance(host, port, exclList);
    }

    private static Constructor proxyPropertiesConstructor()
            throws ClassNotFoundException, NoSuchMethodException {
        return Class.forName("android.net.ProxyProperties").getConstructor(String.class, int.class, String.class);
    }

}
