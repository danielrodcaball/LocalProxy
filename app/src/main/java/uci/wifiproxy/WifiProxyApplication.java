package uci.wifiproxy;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by daniel on 17/09/17.
 */

public class WifiProxyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
