package uci.wifiproxy.proxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;

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

    private static final String SHARED_PREFERENCES_DONT_SHOW_DIALOG_AGAIN = "dontShowDialogAgain";

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
    public void startProxy(@NonNull final String username, @NonNull final String password, @NonNull final String profileId,
                           @NonNull boolean rememberPass, @Nullable final boolean setGlobalProxy) {

        boolean isValidData = validateData(username, password, profileId);

        if (isValidData) {

            saveUpdateUser(username, password, rememberPass);
            saveConfiguration(username, profileId, setGlobalProxy);

            mProfileLocalDataSource.getProfile(profileId, new ProfilesDataSource.GetProfileCallback() {
                @Override
                public void onProfileLoaded(Profile profile) {
                    mProxyView.startProxyService(username, password,
                            profile.getDomain(), profile.getServer(), profile.getInPort(),
                            profile.getOutPort(), profile.getBypass(), profile.getAuthScheme(), setGlobalProxy);
                }

                @Override
                public void onDataNoAvailable() {
                    //never happens in this scenario
                }
            });

            mProxyView.disableAllViews();
            mProxyView.setStopView();
        }
    }

    @Override
    public void stopProxy() {
        mProxyView.stopProxyService();
        mProxyView.enableAllViews();
        mProxyView.setPlayView();
    }

    private void saveUpdateUser(final String username, final String password, final boolean rememberPass) {
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


    private boolean validateData(String user, String pass, String profileId) {
        boolean isValid = true;
        if (Strings.isNullOrEmpty(user)) {
            mProxyView.setUsernameEmptyError();
            isValid = false;
        }
        if (Strings.isNullOrEmpty(pass)) {
            mProxyView.setPasswordEmptyError();
            isValid = false;
        }
        if (Strings.isNullOrEmpty(profileId)) {
            mProxyView.setProfileNoSelectedError();
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void onDestroy() {
        mProfileLocalDataSource.releaseResources();
        mUsersLocalDataSource.releaseResources();
    }

    @Override
    public void addNewProfile() {
        mProxyView.showAddProfile();
    }

    @Override
    public void goToWifiConfDialog() {
        if (mSharedPreferences.getBoolean(SHARED_PREFERENCES_DONT_SHOW_DIALOG_AGAIN, false)){
            goToWifiSettings(false);
        }
        else{
            mProxyView.showWifiConfDialog();
        }
    }

    @Override
    public void goToWifiSettings(boolean dontShowAgain) {
        if (dontShowAgain){
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(SHARED_PREFERENCES_DONT_SHOW_DIALOG_AGAIN, true);
            editor.apply();
        }
        mProxyView.startWifiConfActivity();
    }

    @Override
    public void start() {
        loadUsers();
        loadProfiles();
        loadLastConfiguration();

        if (mProxyView.isProxyServiceRunning()) {
            mProxyView.setStopView();
            mProxyView.disableAllViews();
        } else {
            mProxyView.setPlayView();
            mProxyView.enableAllViews();
        }
    }

    private void loadUsers() {
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

    private void loadProfiles() {
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

    private void saveConfiguration(String username, final String profileId, final boolean setGlobProxy) {

        mUsersLocalDataSource.getUserByUsername(username, new UsersDataSource.GetUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(SHARED_PREFERENCES_USER_ID, user.getId());
                editor.putString(SHARED_PREFERENCES_PROFILE_ID, profileId);
                editor.putBoolean(SHARED_PREFERENCES_GLOBAL_PROXY, setGlobProxy);
                editor.apply();
            }

            @Override
            public void onDataNoAvailable() {
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString(SHARED_PREFERENCES_USER_ID, "");
                editor.putString(SHARED_PREFERENCES_PROFILE_ID, profileId);
                editor.putBoolean(SHARED_PREFERENCES_GLOBAL_PROXY, setGlobProxy);
                editor.apply();
            }
        });

    }

    private void loadLastConfiguration() {
        String userId = mSharedPreferences.getString(SHARED_PREFERENCES_USER_ID, "");
        if (!Strings.isNullOrEmpty(userId)) {
            mUsersLocalDataSource.getUser(userId, new UsersDataSource.GetUserCallback() {
                @Override
                public void onUserLoaded(User user) {
                    mProxyView.setUsername(user.getUsername());
                    if (!Strings.isNullOrEmpty(user.getPassword())) {
                        mProxyView.setPassword(user.getPassword());
                        mProxyView.setRememberPassword(true);
                    } else {
                        mProxyView.setPassword("");
                        mProxyView.setRememberPassword(false);
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

        boolean globProxy = mSharedPreferences.getBoolean(SHARED_PREFERENCES_GLOBAL_PROXY, true);
        mProxyView.setGlobalProxyChecked(globProxy);
    }
}
