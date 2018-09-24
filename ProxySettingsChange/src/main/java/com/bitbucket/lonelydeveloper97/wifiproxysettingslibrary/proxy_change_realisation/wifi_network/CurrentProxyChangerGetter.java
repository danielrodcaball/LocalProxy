package com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network;

import android.content.Context;

import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.exceptions.NullWifiConfigurationException;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.exceptions.ApiNotSupportedException;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.wifi_proxy_changing_realisations.ProxyChanger;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.wifi_proxy_changing_realisations.api_from_15_to_19.WifiConfigurationForApiFrom15To19;
import com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.wifi_proxy_changing_realisations.api_from_21_to_22.WifiConfigurationForApiFrom21To22;


public abstract class CurrentProxyChangerGetter {

    public static ProxyChanger chooseProxyChangerForCurrentApi(Context context)
            throws ApiNotSupportedException, NoSuchFieldException, IllegalAccessException,
            NullWifiConfigurationException {
        if (ApiChecker.isJellyBeanOrKitkat()) {
            return WifiConfigurationForApiFrom15To19.createFromCurrentContext(context);
        } else if (ApiChecker.isLolipop()) {
            return WifiConfigurationForApiFrom21To22.createFromCurrentContext(context);
        } else {
            throw new ApiNotSupportedException();
        }
    }

}
