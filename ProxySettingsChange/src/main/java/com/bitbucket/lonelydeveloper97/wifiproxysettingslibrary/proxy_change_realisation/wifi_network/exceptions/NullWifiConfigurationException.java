package com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.exceptions;


public class NullWifiConfigurationException extends Exception {

    public NullWifiConfigurationException() {
        super("WiFi configuration was null. \n" +
                "If you are trying to change current network settings - check your connection.");
    }

}
