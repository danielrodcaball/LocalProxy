package uci.wifiproxy.data.proxyConfiguration;

/**
 * Created by daniel on 6/11/17.
 */

public class ProxyConfiguration {

    private String userId;

    private String profileId;

    private int localPort;

    private boolean setGlobalProxy;


    public ProxyConfiguration(String userId, String profileId, int localPort, boolean setGlobalProxy) {
        this.userId = userId;
        this.profileId = profileId;
        this.localPort = localPort;
        this.setGlobalProxy = setGlobalProxy;
    }


    public String getUserId() {
        return userId;
    }

    public String getProfileId() {
        return profileId;
    }

    public int getLocalPort() {
        return localPort;
    }

    public boolean isSetGlobalProxy() {
        return setGlobalProxy;
    }

}
