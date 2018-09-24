package com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.wifi_proxy_changing_realisations.api_from_15_to_19;

import android.content.Context;
import android.net.LinkProperties;

import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.exceptions.NullWifiConfigurationException;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.exceptions.ApiNotSupportedException;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.exceptions.WifiProxyNotSettedException;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.reflection_realisation.ReflectionHelper;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.wifi_proxy_changing_realisations.BaseWifiConfiguration;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.wifi_proxy_changing_realisations.ProxyChanger;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.wifi_proxy_changing_realisations.ProxySettings;

import java.lang.reflect.InvocationTargetException;

public class WifiConfigurationForApiFrom15To19 extends BaseWifiConfiguration implements ProxyChanger {

    private ProxyPropertiesContainer proxyPropertiesContainer;


    public WifiConfigurationForApiFrom15To19(Context context)
            throws NoSuchFieldException, IllegalAccessException, NullWifiConfigurationException {
        super(context);
        this.proxyPropertiesContainer = new ProxyPropertiesContainer(getCurrentProxyProperties());
    }

    public static WifiConfigurationForApiFrom15To19 createFromCurrentContext(Context context)
            throws NoSuchFieldException, IllegalAccessException, NullWifiConfigurationException {
        return new WifiConfigurationForApiFrom15To19(context);
    }

    @Override
    public void setProxySettings(ProxySettings proxySettings)
            throws NoSuchFieldException, IllegalAccessException {
        ReflectionHelper.setEnumField(wifiConfiguration, proxySettings.getValue(), "proxySettings");
    }

    @Override
    public ProxySettings getProxySettings()
            throws NoSuchFieldException, IllegalAccessException {
        return ProxySettings.valueOf(String.valueOf(ReflectionHelper.getDeclaredField(wifiConfiguration, "proxySettings")));
    }

    @Override
    public void setProxyHostAndPort(String host, int port)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException, NoSuchFieldException {
        proxyPropertiesContainer = new ProxyPropertiesContainer(host, port);
        ReflectionHelper.getMethodAndInvokeIt(
                getLinkProperties(),
                "setHttpProxy",
                proxyPropertiesContainer.getProxyProperties());
    }

    @Override
    public String getProxyHost()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            ApiNotSupportedException, NoSuchFieldException {
        if (proxyPropertiesContainer == null)
            throw new WifiProxyNotSettedException();
        return proxyPropertiesContainer.getHost();
    }

    @Override
    public int getProxyPort()
            throws ApiNotSupportedException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, NoSuchFieldException {
        if (proxyPropertiesContainer == null)
            throw new WifiProxyNotSettedException();
        return proxyPropertiesContainer.getPort();
    }

    @Override
    public boolean isProxySetted()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            ApiNotSupportedException, NoSuchFieldException {
        return !(proxyPropertiesContainer == null);
    }

    private LinkProperties getLinkProperties()
            throws NoSuchFieldException, IllegalAccessException {
        return (LinkProperties) ReflectionHelper.getField(wifiConfiguration, "linkProperties");
    }

    private Object getCurrentProxyProperties()
            throws NoSuchFieldException, IllegalAccessException {
        return ReflectionHelper.getDeclaredField(getLinkProperties(), "mHttpProxy");
    }

}
