package uci.localproxy.proxyscreen;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.base.Strings;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.auth.AuthScope;
import cz.msebera.android.httpclient.auth.NTCredentials;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.CredentialsProvider;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;
import cz.msebera.android.httpclient.impl.client.BasicCredentialsProvider;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import uci.localproxy.LocalProxyApplication;
import uci.localproxy.data.firewallRule.FirewallRuleLocalDataSource;
import uci.localproxy.data.pref.AppPreferencesHelper;
import uci.localproxy.data.profile.Profile;
import uci.localproxy.data.profile.source.ProfilesDataSource;
import uci.localproxy.data.profile.source.ProfilesLocalDataSource;
import uci.localproxy.data.user.User;
import uci.localproxy.data.user.UsersDataSource;
import uci.localproxy.data.user.UsersLocalDataSource;
import uci.localproxy.proxycore.ProxyService;
import uci.localproxy.util.network.PortsUtils;

import static uci.localproxy.profilescreens.addeditprofile.AddEditProfilePresenter.MAX_PORTS_LIMIT;
import static uci.localproxy.profilescreens.addeditprofile.AddEditProfilePresenter.MAX_SYSTEM_PORTS_LIMIT;

/**
 * Created by daniel on 20/09/17.
 */

public class ProxyPresenter implements ProxyContract.Presenter {

    private static final int UNKNOWN_HOST = 1;
    private static final int CONNECTION_TIMEOUT = 2;
    private static final int AUTHENTICATION_FAILED = 3;
    private static final int AUTHENTICATION_SUCCEED = 4;
    private static final int CONNECTION_ERROR = 5;


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

        boolean isValidData = validateData(username, password, profileId, localPort);
        if (!isValidData) return;

        if (Build.VERSION.SDK_INT > LocalProxyApplication.MAX_SDK_SUPPORTED_FOR_WIFI_CONF &&
                !mPrefHelper.getDontShowDialogAgain()
                ) {
            mProxyView.showWifiConfDialog();
            return;
        }

        setGlobalProxy = Build.VERSION.SDK_INT <= LocalProxyApplication.MAX_SDK_SUPPORTED_FOR_WIFI_CONF && setGlobalProxy;
        startProxy(username, password, profileId, localPort, rememberPass, setGlobalProxy);
    }

    @Override
    public void startProxyFromHelpDialog(@NonNull String user, @NonNull String pass,
                                         @NonNull String profileID, @NonNull String localPort,
                                         @NonNull boolean rememberPass, @NonNull boolean setGlobalProxy, @NonNull boolean dontShowAgain) {
        if (dontShowAgain) {
            mPrefHelper.setDontShowDialogAgain(true);
        }

        setGlobalProxy = Build.VERSION.SDK_INT <= LocalProxyApplication.MAX_SDK_SUPPORTED_FOR_WIFI_CONF && setGlobalProxy;

        boolean isValidData = validateData(user, pass, profileID, localPort);
        if (!isValidData) return;
        startProxy(user, pass, profileID, localPort, rememberPass, setGlobalProxy);
    }

    private void startProxy(@NonNull final String username, @NonNull final String password, @NonNull final String profileId,
                            @NonNull final String localPort, @NonNull boolean rememberPass,
                            @Nullable final boolean setGlobalProxy) {


        saveUpdateUser(username, password, rememberPass);
        saveConfiguration(username, profileId, localPort, setGlobalProxy);

//        if (!mProxyView.isConnectedToAWifi()) {
//            mProxyView.showNetworkError();
//            return;
//        }

        mProfileLocalDataSource.getProfile(profileId, new ProfilesDataSource.GetProfileCallback() {
            @Override
            public void onProfileLoaded(Profile profile) {

                String user = username;
                String domain = "";

                if (username.contains("\\")) {
                    int backSlashPos = username.indexOf("\\");
                    domain = username.substring(0, backSlashPos);
                    user = username.substring(backSlashPos + 1, username.length());
                }

                CredentialsCheckTask task = new CredentialsCheckTask(
                        user,
                        password,
                        profile.getHost(),
                        profile.getInPort(),
                        Integer.parseInt(localPort),
                        profile.getBypass(),
                        domain,
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

        if (!Strings.isNullOrEmpty(localPort) &&
                Integer.parseInt(localPort) > MAX_SYSTEM_PORTS_LIMIT &&
                Integer.parseInt(localPort) <= MAX_PORTS_LIMIT &&
                !isLocalPortAvailable(Integer.parseInt(localPort))) {
            mProxyView.setLocalPortNotAvailable();
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

        if (ProxyService.IS_SERVICE_RUNNING) {
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

    @Override
    public void startServiceResult(int message) {
        if (message == ProxyService.ERROR_STARTING_SERVICE) {
            mProxyView.showErrorStartingService();
            stopProxy();
        }
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

    private boolean isLocalPortAvailable(int port) {
        return PortsUtils.isPortAvailable(port);
    }

    private class CredentialsCheckTask extends AsyncTask<Object, Object, Integer> {

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
            mProxyView.showProgressDialog(true);
        }

        @Override
        protected Integer doInBackground(Object... objects) {
            try {
                CredentialsProvider credentials = new BasicCredentialsProvider();

                credentials.setCredentials(new AuthScope(AuthScope.ANY),
                        new NTCredentials(username, password, InetAddress.getLocalHost().getHostName(),
                                (Strings.isNullOrEmpty(domain) ? null : domain)
                        )
                );
                CloseableHttpClient client = HttpClientBuilder.create()
                        .setProxy(new HttpHost(proxyHost, proxyPort))
                        .setDefaultCredentialsProvider(credentials)
                        .disableRedirectHandling()
                        .build();

                HttpResponse response = client.execute(new HttpGet("http://google.com"));
//                Log.e("auth_status_code", response.getStatusLine().getStatusCode() + "");

                if (response.getStatusLine().getStatusCode() == 407) {
                    return AUTHENTICATION_FAILED;
                }

                return AUTHENTICATION_SUCCEED;

            } catch (ClientProtocolException e) {
                e.printStackTrace();
                return CONNECTION_ERROR;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return UNKNOWN_HOST;
            } catch (ConnectTimeoutException e) {
                e.printStackTrace();
                return CONNECTION_TIMEOUT;
            } catch (IOException e) {
                e.printStackTrace();
                return CONNECTION_ERROR;
            }

        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            mProxyView.showProgressDialog(false);
            switch (result) {
                case AUTHENTICATION_SUCCEED:
                    //start proxy
                    mProxyView.startProxyService(username, password,
                            proxyHost,
                            proxyPort,
                            localPort,
                            bypass,
                            (Strings.isNullOrEmpty(domain) ? null : domain),
                            setGlobProxy
                    );
                    mProxyView.disableAllViews();
                    mProxyView.setStopView();
                    break;
                case AUTHENTICATION_FAILED:
                    mProxyView.showWrongCredentialsDialog();
                    break;
                case UNKNOWN_HOST:
                    mProxyView.showUnknownHostError();
                    break;
                case CONNECTION_TIMEOUT:
                    mProxyView.showConnectionTimeOutError();
                    break;
                case CONNECTION_ERROR:
                    mProxyView.showConnectionError();
                    break;
            }
        }
    }
}
