package uci.wifiproxy.proxyscreen;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import uci.wifiproxy.WifiProxyApplication;
import uci.wifiproxy.data.firewallRule.FirewallRuleLocalDataSource;
import uci.wifiproxy.data.pref.AppPreferencesHelper;
import uci.wifiproxy.data.profile.Profile;
import uci.wifiproxy.data.profile.source.ProfilesDataSource;
import uci.wifiproxy.data.profile.source.ProfilesLocalDataSource;
import uci.wifiproxy.data.user.User;
import uci.wifiproxy.data.user.UsersDataSource;
import uci.wifiproxy.data.user.UsersLocalDataSource;

import static uci.wifiproxy.profilescreens.addeditprofile.AddEditProfilePresenter.MAX_PORTS_LIMIT;
import static uci.wifiproxy.profilescreens.addeditprofile.AddEditProfilePresenter.MAX_SYSTEM_PORTS_LIMIT;

/**
 * Created by daniel on 20/09/17.
 */

public class ProxyPresenter implements ProxyContract.Presenter {

    @NonNull
    private ProxyContract.View mProxyView;

    private ProfilesLocalDataSource mProfileLocalDataSource;

    private UsersLocalDataSource mUsersLocalDataSource;

    private FirewallRuleLocalDataSource mFirewallRulesLocalDataSource;

    @NonNull
    private AppPreferencesHelper mPrefHelper;


    public ProxyPresenter(@NonNull ProxyContract.View proxyView,
                          @NonNull AppPreferencesHelper prefHelper) {
        mProxyView = proxyView;
        mPrefHelper = prefHelper;
        mProfileLocalDataSource = ProfilesLocalDataSource.newInstance();
        mUsersLocalDataSource = UsersLocalDataSource.newInstance();
        mFirewallRulesLocalDataSource = FirewallRuleLocalDataSource.newInstance();

        mProxyView.setPresenter(this);
    }

    @Override
    public void startProxyFromFabButton(@NonNull String username, @NonNull String password, @NonNull String profileId,
                                        @NonNull String localPort, @NonNull boolean rememberPass,
                                        @Nullable boolean setGlobalProxy) {

        if (Build.VERSION.SDK_INT > WifiProxyApplication.MAX_SDK_SUPPORTED_FOR_WIFI_CONF &&
                !mPrefHelper.getDontShowDialogAgain()
                ) {
            mProxyView.showWifiConfDialog();
            return;
        }

        setGlobalProxy = Build.VERSION.SDK_INT <= WifiProxyApplication.MAX_SDK_SUPPORTED_FOR_WIFI_CONF && setGlobalProxy;
        startProxy(username, password, profileId, localPort, rememberPass, setGlobalProxy);
    }

    @Override
    public void startProxyFromHelpDialog(@NonNull String user, @NonNull String pass,
                                         @NonNull String profileID, @NonNull String localPort,
                                         @NonNull boolean rememberPass, @NonNull boolean setGlobalProxy, @NonNull boolean dontShowAgain) {
        if (dontShowAgain) {
            mPrefHelper.setDontShowDialogAgain(true);
        }

        setGlobalProxy = Build.VERSION.SDK_INT <= WifiProxyApplication.MAX_SDK_SUPPORTED_FOR_WIFI_CONF && setGlobalProxy;
        startProxy(user, pass, profileID, localPort, rememberPass, setGlobalProxy);
    }

    private void startProxy(@NonNull final String username, @NonNull final String password, @NonNull final String profileId,
                            @NonNull final String localPort, @NonNull boolean rememberPass,
                            @Nullable final boolean setGlobalProxy) {

        boolean isValidData = validateData(username, password, profileId, localPort);

        if (!isValidData) return;

        saveUpdateUser(username, password, rememberPass);
        saveConfiguration(username, profileId, localPort, setGlobalProxy);

        mProfileLocalDataSource.getProfile(profileId, new ProfilesDataSource.GetProfileCallback() {
            @Override
            public void onProfileLoaded(Profile profile) {

                CredentialsCheckTask task = new CredentialsCheckTask(
                        username,
                        password,
                        profile.getHost(),
                        profile.getInPort(),
                        Integer.parseInt(localPort),
                        profile.getBypass(),
                        profile.getDomain(),
                        setGlobalProxy
                );

                task.execute();

            }

            @Override
            public void onDataNoAvailable() {
                //never happens in this scenario
            }
        });
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


    private boolean validateData(String user, String pass, String profileId, String localPort) {
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

        if (Strings.isNullOrEmpty(localPort)) {
            mProxyView.setLocalPortEmptyError();
            isValid = false;
        }

        if (!Strings.isNullOrEmpty(localPort) &&
                (Integer.parseInt(localPort) <= MAX_SYSTEM_PORTS_LIMIT ||
                        Integer.parseInt(localPort) > MAX_PORTS_LIMIT)) {

            mProxyView.setLocalPortOutOfRangeError();
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void onDestroy() {
        mProfileLocalDataSource.releaseResources();
        mUsersLocalDataSource.releaseResources();
        mFirewallRulesLocalDataSource.releaseResources();
    }

    @Override
    public void addNewProfile() {
        mProxyView.showAddProfile();
    }

    @Override
    public void goToWifiConfDialog() {
        if (mPrefHelper.getDontShowDialogAgain()) {
            goToWifiSettings(false);
        } else {
            mProxyView.showWifiConfDialog();
        }
    }

    @Override
    public void goToWifiSettings(boolean dontShowAgain) {
        if (dontShowAgain) {
            mPrefHelper.setDontShowDialogAgain(true);
        }
        mProxyView.startWifiConfActivity();
    }

    @Override
    public void filterUsers(String constraint) {
        mUsersLocalDataSource.filterByUsernameUsers(constraint, new UsersDataSource.FilterUsersCallback() {
            @Override
            public void onUsersFiltered(List<User> users) {
                mProxyView.setUsers(users);
            }

            @Override
            public void onDataNoAvailable() {
                mProxyView.setUsers(new ArrayList<User>(0));
            }
        });
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

    @Override
    public void loadUsers() {
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

    private void saveConfiguration(String username, final String profileId, final String localPort, final boolean setGlobProxy) {

        mUsersLocalDataSource.getUserByUsername(username, new UsersDataSource.GetUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                mPrefHelper.setCurrentUserId(user.getId());
                mPrefHelper.setCurrentProfileId(profileId);
                mPrefHelper.setCurrentLocalPort(Integer.parseInt(localPort));
                mPrefHelper.setCurrentIsSetGlobProxy(setGlobProxy);
            }

            @Override
            public void onDataNoAvailable() {
                //Never happens in this scenario, because the user was already saved
                //at the beginning in the start proxy method, its to say the user always
                //will be loaded
                mPrefHelper.setCurrentUserId("");
                mPrefHelper.setCurrentProfileId(profileId);
                mPrefHelper.setCurrentLocalPort(Integer.parseInt(localPort));
                mPrefHelper.setCurrentIsSetGlobProxy(setGlobProxy);
            }
        });

    }


    private void loadLastConfiguration() {

        String userId = mPrefHelper.getCurrentUserId();

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

        String profileId = mPrefHelper.getCurrentProfileId();
        if (!Strings.isNullOrEmpty(profileId)) {
            mProxyView.setSpinnerProfileSelected(profileId);
        }

        int localPort = mPrefHelper.getCurrentLocalPort();
        if (localPort > -1) {
            mProxyView.setLocalPort(String.valueOf(localPort));
        }

        boolean globProxy = mPrefHelper.getCurrentIsSetGlobProxy();
        mProxyView.setGlobalProxyChecked(globProxy);
    }

    private class CredentialsCheckTask extends AsyncTask<Object, Object, Boolean> {

        private String username;
        private String password;
        private String proxyHost;
        private int proxyPort;
        private int localPort;
        private String bypass;
        private String domain;
        private boolean setGlobProxy;

        public CredentialsCheckTask(String username, String password, String proxyHost, int proxyPort, int localPort, String bypass, String domain, boolean setGlobProxy) {
            this.username = username;
            this.password = password;
            this.proxyHost = proxyHost;
            this.proxyPort = proxyPort;
            this.localPort = localPort;
            this.bypass = bypass;
            this.domain = domain;
            this.setGlobProxy = setGlobProxy;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            mProxyView.showProgressDialog(true);
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mProxyView.showProgressDialog(false);
            if (aBoolean) {
                //start proxy
                mProxyView.startProxyService(username, password,
                        proxyHost,
                        proxyPort,
                        localPort,
                        bypass,
                        domain,
                        setGlobProxy
                );

                mProxyView.disableAllViews();
                mProxyView.setStopView();
            } else {
                mProxyView.showWrongCredentialsDialog();
            }
        }
    }
}
