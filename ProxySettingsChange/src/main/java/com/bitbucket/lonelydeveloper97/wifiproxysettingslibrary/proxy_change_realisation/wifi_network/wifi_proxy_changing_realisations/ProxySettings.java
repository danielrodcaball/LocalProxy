package com.bitbucket.lonelydeveloper97.wifiproxysettingslibrary.proxy_change_realisation.wifi_network.wifi_proxy_changing_realisations;

public enum ProxySettings {

    /* No proxy is to be used. Any existing proxy settings
     * should be cleared. */
    NONE("NONE"),
    /* Use statically configured proxy. Configuration can be accessed
     * with httpProxy. */
    STATIC("STATIC"),
    /* no proxy details are assigned, this is used to indicate
     * that any existing proxy settings should be retained */
    UNASSIGNED("UNASSIGNED"),
    /* Use a Pac based proxy.
     */
    PAC("PAC");


    String value = "";


    ProxySettings(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}