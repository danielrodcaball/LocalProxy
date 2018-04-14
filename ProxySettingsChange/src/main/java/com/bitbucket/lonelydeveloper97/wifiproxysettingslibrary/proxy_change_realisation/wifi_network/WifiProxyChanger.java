package com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.NetworkHelper;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.exceptions.NullWifiConfigurationException;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.exceptions.ApiNotSupportedException;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.wifi_proxy_changing_realisations.ProxyChanger;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.wifi_proxy_changing_realisations.ProxySettings;

import java.lang.reflect.InvocationTargetException;

public abstract class WifiProxyChanger {

    public static void changeWifiStaticProxySettings(String host, int port, Context context)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException, NoSuchFieldException,
            ApiNotSupportedException, NullWifiConfigurationException {
        updateWifiWithNewConfiguration(
                getCurrentWifiConfigurationWithUpdatedSettings(host, port, ProxySettings.STATIC, context),
                context);
    }

    public static void clearProxySettings(Context context)
            throws IllegalAccessException, ApiNotSupportedException, NoSuchFieldException,
            NullWifiConfigurationException, ClassNotFoundException, NoSuchMethodException,
            InstantiationException, InvocationTargetException {
        updateWifiWithNewConfiguration(
                getCurrentWifiConfigurationWithUpdatedSettings("", 0, ProxySettings.NONE, context),
                context);
    }

    private static WifiConfiguration getCurrentWifiConfigurationWithUpdatedSettings(String host, int port, ProxySettings proxySettings, Context context)
            throws ApiNotSupportedException, IllegalAccessException, NullWifiConfigurationException,
            NoSuchFieldException, ClassNotFoundException, NoSuchMethodException,
            InstantiationException, InvocationTargetException {
        ProxyChanger proxyChanger = CurrentProxyChangerGetter.chooseProxyChangerForCurrentApi(context);
        proxyChanger.setProxyHostAndPort(host, port);
        proxyChanger.setProxySettings(proxySettings);
        return proxyChanger.getWifiConfiguration();
    }


    private static void updateWifiWithNewConfiguration(WifiConfiguration wifiConfiguration, Context context) {
        WifiManager currentWifiManager = NetworkHelper.getWifiManager(context);
        currentWifiManager.updateNetwork(wifiConfiguration);
        currentWifiManager.saveConfiguration();
        currentWifiManager.disconnect();
        currentWifiManager.reconnect();
    }

}
