package com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.wifi_proxy_changing_realisations.api_from_15_to_19;

import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.reflection_realisation.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;


public class ProxyPropertiesContainer {

    Object proxyProperties;


    ProxyPropertiesContainer(Object proxyProperties) {
        this.proxyProperties = proxyProperties;
    }

    ProxyPropertiesContainer(String host, int port)
            throws ClassNotFoundException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {
        this(host, port, null);
    }

    ProxyPropertiesContainer(String host, int port, String exclList)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {
        this(ProxyPropertiesConstructor.proxyProperties(host, port, exclList));
    }

    public String getHost()
            throws NoSuchFieldException, IllegalAccessException {
        return (String) ReflectionHelper.getDeclaredField(proxyProperties, "mHost");
    }

    public int getPort()
            throws NoSuchFieldException, IllegalAccessException {
        return (int) ReflectionHelper.getDeclaredField(proxyProperties, "mPort");
    }

    public String getExclusionList()
            throws NoSuchFieldException, IllegalAccessException {
        return (String) ReflectionHelper.getDeclaredField(proxyProperties, "mExclusionList");
    }

    public Object getProxyProperties() {
        return proxyProperties;
    }

}
