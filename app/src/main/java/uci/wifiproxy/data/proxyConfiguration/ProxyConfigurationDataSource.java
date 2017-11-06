package uci.wifiproxy.data.proxyConfiguration;

import android.content.Context;
import android.content.SharedPreferences;

import uci.wifiproxy.WifiProxyApplication;

/**
 * Created by daniel on 6/11/17.
 */

public class ProxyConfigurationDataSource {

    public static ProxyConfigurationDataSource INSTANCE;


    private static final String SHARED_PREFERENCES_USER_ID = "userId";

    private static final String SHARED_PREFERENCES_GLOBAL_PROXY = "globalProxy";

    private static final String SHARED_PREFERENCES_PROFILE_ID = "profile_id";

    private static final String SHARED_PREFERENCES_LOCAL_PORT = "localPort";

    private SharedPreferences mSharedPreferences;

    private ProxyConfigurationDataSource(Context context){
        mSharedPreferences = context.getSharedPreferences(WifiProxyApplication.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static ProxyConfigurationDataSource newInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = new ProxyConfigurationDataSource(context);
        }
        return INSTANCE;
    }

    public void releaseResources(){
        INSTANCE = null;
    }

    public ProxyConfiguration getProxyConfiguration(){
        String userId = mSharedPreferences.getString(SHARED_PREFERENCES_USER_ID, "");
        String profileId = mSharedPreferences.getString(SHARED_PREFERENCES_PROFILE_ID, "");
        int localPort = mSharedPreferences.getInt(SHARED_PREFERENCES_LOCAL_PORT, 0);
        boolean setGlobalProxy = mSharedPreferences.getBoolean(SHARED_PREFERENCES_GLOBAL_PROXY, true);

        return new ProxyConfiguration(userId, profileId, localPort, setGlobalProxy);
    }

    public void saveProxyConfiguration(ProxyConfiguration configuration){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_USER_ID, configuration.getUserId());
        editor.putString(SHARED_PREFERENCES_PROFILE_ID, configuration.getProfileId());
        editor.putInt(SHARED_PREFERENCES_LOCAL_PORT, configuration.getLocalPort());
        editor.putBoolean(SHARED_PREFERENCES_GLOBAL_PROXY, configuration.isSetGlobalProxy());
        editor.apply();
    }

}
