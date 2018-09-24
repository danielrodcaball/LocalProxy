package com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.wifi_proxy_changing_realisations;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.exceptions.NullWifiConfigurationException;

import java.util.ArrayList;
import java.util.List;

public class BaseWifiConfiguration {

    protected WifiConfiguration wifiConfiguration;


    protected BaseWifiConfiguration(WifiConfiguration wifiConfiguration)
            throws NullWifiConfigurationException {
        if (wifiConfiguration == null)
            throw new NullWifiConfigurationException();
        this.wifiConfiguration = wifiConfiguration;
    }

    protected BaseWifiConfiguration(Context context)
            throws NullWifiConfigurationException {
        this(getCurrentWifiConfigurationFromContext(context));
    }

    public WifiConfiguration getWifiConfiguration() {
        return wifiConfiguration;
    }

    private static WifiConfiguration getCurrentWifiConfigurationFromContext(Context context) {
        final WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> wifiConfigurationList = manager.getConfiguredNetworks();
        if (!manager.isWifiEnabled() || wifiConfigurationList == null || wifiConfigurationList.isEmpty())
            return null;
        return findWifiConfigurationByNetworkId(wifiConfigurationList, manager.getConnectionInfo().getNetworkId());

    }

    private static WifiConfiguration findWifiConfigurationByNetworkId(List<WifiConfiguration> wifiConfigurationList, int networkId) {
        for (WifiConfiguration wifiConf : wifiConfigurationList) {
            if (wifiConf.networkId == networkId)
                return wifiConf;
        }
        return null;
    }

}
