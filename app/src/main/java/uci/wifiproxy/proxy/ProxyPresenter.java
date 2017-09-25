package uci.wifiproxy.proxy;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

import uci.wifiproxy.R;
import uci.wifiproxy.data.profile.Profile;
import uci.wifiproxy.data.profile.source.ProfilesDataSource;
import uci.wifiproxy.data.profile.source.ProfilesLocalDataSource;
import uci.wifiproxy.data.user.User;
import uci.wifiproxy.data.user.UsersDataSource;
import uci.wifiproxy.data.user.UsersLocalDataSource;

/**
 * Created by daniel on 20/09/17.
 */

public class ProxyPresenter implements ProxyContract.Presenter {

    private static final String SHARED_PREFERENCES_USER_ID = "userId";

    private static final String SHARED_PREFERENCES_GLOBAL_PROXY = "globalProxy";

    private static final String SHARED_PREFERENCES_PROFILE_ID = "profile_id";

    private ProxyContract.View mProxyView;

    private ProfilesLocalDataSource mProfileLocalDataSource;

    private UsersLocalDataSource mUsersLocalDataSource;

    private SharedPreferences mSharedPreferences;


    public ProxyPresenter(ProxyContract.View proxyView,
                          SharedPreferences sharedPreferences) {
        mProxyView = proxyView;
        mSharedPreferences = sharedPreferences;
        mProfileLocalDataSource = ProfilesLocalDataSource.newInstance();
        mUsersLocalDataSource = UsersLocalDataSource.newInstance();

        mProxyView.setPresenter(this);
    }

    @Override
    public void startProxy(@NonNull String username, @NonNull String password, @NonNull String profileId,
                           @NonNull boolean rememberPass, @Nullable boolean setGlobalProxy) {

        boolean isValidData = validateData(username, password, profileId);

        if (isValidData){

            saveUpdateUser(username, password, rememberPass);

            //do proxy stuff

            mProxyView.disableAllViews();
            mProxyView.setStopView();
        }
    }

    private void saveUpdateUser(final String username, final String password, final boolean rememberPass){
        mUsersLocalDataSource.getUserByUsername(username, new UsersDataSource.GetUserCallback() {
            @Override
            public void onUserLoaded(User user) {

                User userToUpdate = (rememberPass) ? User.newUser(user.getId(), user.getUsername(), password)
                        : User.newUser(user.getId(), user.getUsername(), "");

                mUsersLocalDataSource.updateUser(userToUpdate, new UsersDataSource.SaveUpdateUserCallback() {
                    @Override
                    public void onUserSaved() {
                        //Nothing Ok
                    }

                    @Override
                    public void onUsernameAlreadyExist() {
                        //Nothing, never happen in this scenario
                    }
                });
            }

            @Override
            public void onDataNoAvailable() {
                User user = (rememberPass) ? User.newUser(username, password) : User.newUser(username, "");
                mUsersLocalDataSource.saveUser(user, new UsersDataSource.SaveUpdateUserCallback() {
                    @Override
                    public void onUserSaved() {
                        //Nothing Ok
                    }

                    @Override
                    public void onUsernameAlreadyExist() {
                        //Nothing, never happen in this scenario
                    }
                });
            }
        });
    }


    private boolean validateData(String user, String pass, String profileId){
        boolean isValid = true;
        if (Strings.isNullOrEmpty(user)){
            mProxyView.setUsernameEmptyError();
            isValid = false;
        }
        if (Strings.isNullOrEmpty(pass)){
            mProxyView.setPasswordEmptyError();
            isValid = false;
        }
        if (Strings.isNullOrEmpty(profileId)){
            mProxyView.setProfileNoSelectedError();
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void stopProxy() {

    }

    @Override
    public void onDestroy() {
        mProfileLocalDataSource.releaseResources();
        mUsersLocalDataSource.releaseResources();
    }

    @Override
    public void filterUsers(String usernameText) {

    }

    @Override
    public void onTouchButtonViewPass(@NonNull int action) {
        if (action == MotionEvent.ACTION_DOWN){
            mProxyView.setPasswordVisibility(true);
        }
        else if(action == MotionEvent.ACTION_UP){
            mProxyView.setPasswordVisibility(false);
        }
    }

    @Override
    public void addNewProfile() {
        mProxyView.showAddProfile();
    }

    @Override
    public void start() {
        loadUsers();
        loadProfiles();
        loadLastConfiguration();
    }

    private void loadUsers(){
        mUsersLocalDataSource.getUsers(new UsersDataSource.LoadUsersCallback() {
            @Override
            public void onUsersLoaded(List<User> users) {
                mProxyView.setUsers(users);
            }

            @Override
            public void onDataNoAvailable() {
                mProxyView.setUsers(new ArrayList<User>(0));
            }
        });
    }

    private void loadProfiles(){
        mProfileLocalDataSource.getProfiles(new ProfilesDataSource.LoadProfilesCallback() {
            @Override
            public void onProfilesLoaded(List<Profile> profiles) {
                mProxyView.setSpinnerProfiles(profiles);
            }

            @Override
            public void onDataNoAvailable() {
                mProxyView.showNoProfilesView();
            }
        });
    }

    private void loadLastConfiguration(){
        String userId = mSharedPreferences.getString(SHARED_PREFERENCES_USER_ID, "");
        if (Strings.isNullOrEmpty(userId)){
             mUsersLocalDataSource.getUser(userId, new UsersDataSource.GetUserCallback() {
                 @Override
                 public void onUserLoaded(User user) {
                     mProxyView.setUsername(user.getUsername());
                     if (!Strings.isNullOrEmpty(user.getPassword())){
                         mProxyView.setPassword(user.getPassword());
                         mProxyView.setRememberPassword(true);
                     }
                 }

                 @Override
                 public void onDataNoAvailable() {
                    mProxyView.setUsername("");
                    mProxyView.setPassword("");
                    mProxyView.setRememberPassword(false);
                 }
             });
        }

        String profileId = mSharedPreferences.getString(SHARED_PREFERENCES_PROFILE_ID, "");
        mProxyView.setSpinnerProfileSelected(profileId);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            boolean globProxy = mSharedPreferences.getBoolean(SHARED_PREFERENCES_GLOBAL_PROXY, true);
            mProxyView.setGlobalProxyChecked(globProxy);
        }
    }
}
