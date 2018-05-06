package uci.localproxy.data.pref;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by daniel on 12/11/17.
 */

public class AppPreferencesHelper {

    private static final String SHARED_PREFERENCES_NAME = "WifiProxy.conf";

    private static final String SHARED_PREFERENCES_USER_ID = "userId";

    private static final String SHARED_PREFERENCES_GLOBAL_PROXY = "globalProxy";

    private static final String SHARED_PREFERENCES_PROFILE_ID = "profile_id";

    private static final String SHARED_PREFERENCES_DONT_SHOW_DIALOG_AGAIN = "dontShowDialogAgain";

    private static final String SHARED_PREFERENCES_LOCAL_PORT = "localPort";

    private static AppPreferencesHelper INSTANCE;

    private final SharedPreferences mPref;

    public static AppPreferencesHelper getInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = new AppPreferencesHelper(context);
        }
        return INSTANCE;
    }

    private AppPreferencesHelper(Context context){
        mPref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public String getCurrentUserId(){
        return mPref.getString(SHARED_PREFERENCES_USER_ID, "");
    }

    public String getCurrentProfileId(){
        return mPref.getString(SHARED_PREFERENCES_PROFILE_ID, "");
    }

    public int getCurrentLocalPort(){
        return mPref.getInt(SHARED_PREFERENCES_LOCAL_PORT, -1);
    }

    public boolean getCurrentIsSetGlobProxy(){
        return mPref.getBoolean(SHARED_PREFERENCES_GLOBAL_PROXY, true);
    }

    public boolean getDontShowDialogAgain(){
        return mPref.getBoolean(SHARED_PREFERENCES_DONT_SHOW_DIALOG_AGAIN, false);
    }

    public void setCurrentUserId(String userId){
        mPref.edit().putString(SHARED_PREFERENCES_USER_ID, userId).apply();
    }

    public void setCurrentProfileId(String profileId){
        mPref.edit().putString(SHARED_PREFERENCES_PROFILE_ID, profileId).apply();
    }

    public void setCurrentLocalPort(int localPort){
        mPref.edit().putInt(SHARED_PREFERENCES_LOCAL_PORT, localPort).apply();
    }

    public void setCurrentIsSetGlobProxy(boolean isSetGlobProxy){
        mPref.edit().putBoolean(SHARED_PREFERENCES_GLOBAL_PROXY, isSetGlobProxy).apply();
    }

    public void setDontShowDialogAgain(boolean dontShowDialogAgain){
        mPref.edit().putBoolean(SHARED_PREFERENCES_DONT_SHOW_DIALOG_AGAIN, dontShowDialogAgain).apply();
    }

}
