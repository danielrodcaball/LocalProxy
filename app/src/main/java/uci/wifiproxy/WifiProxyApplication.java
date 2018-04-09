package uci.wifiproxy;

import android.app.Application;
import android.os.Build;

import io.realm.Realm;

/**
 * Created by daniel on 17/09/17.
 */

public class WifiProxyApplication extends Application {
    public static final int MAX_SDK_SUPPORTED_FOR_WIFI_CONF = Build.VERSION_CODES.LOLLIPOP_MR1;
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
