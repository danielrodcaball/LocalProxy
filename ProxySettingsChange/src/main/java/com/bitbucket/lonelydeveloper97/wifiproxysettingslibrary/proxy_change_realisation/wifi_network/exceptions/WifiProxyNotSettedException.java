package com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.exceptions;


public class WifiProxyNotSettedException extends IllegalStateException {

    public WifiProxyNotSettedException() {
        super("Wifi proxy not setted for current WifiConfiguration");
    }

}

