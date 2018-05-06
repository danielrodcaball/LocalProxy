package uci.localproxy.profilescreens.addeditprofile;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;

import uci.localproxy.data.profile.Profile;
import uci.localproxy.data.profile.source.ProfilesDataSource;
import uci.localproxy.data.profile.source.ProfilesLocalDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 17/09/17.
 */

public class AddEditProfilePresenter implements AddEditProfileContract.Presenter {

    public final static int MAX_PORTS_LIMIT = 65535;

    public final static int MAX_SYSTEM_PORTS_LIMIT = 1023;

    @NonNull
    private ProfilesLocalDataSource mProfilesDataSource;

    @NonNull
    private AddEditProfileContract.View mAddProfileView;

    @Nullable
    private String mProfileId;

    private boolean mIsDataMissing;

    public AddEditProfilePresenter(@NonNull AddEditProfileContract.View mAddProfileView,
                                   @Nullable String mProfileId, boolean shouldLoadDataFromSource) {
        this.mProfilesDataSource = ProfilesLocalDataSource.newInstance();
        this.mAddProfileView = checkNotNull(mAddProfileView);
        this.mProfileId = mProfileId;
        this.mIsDataMissing = shouldLoadDataFromSource;

        mAddProfileView.setPresenter(this);
    }

    @Override
    public void start() {
        if (!isNewProfile() && mIsDataMissing) {
            populateProfile();
        }
    }

    @Override
    public void saveProfile(String name, String server,
                            String inPort, String bypass) {
        if (isNewProfile()) {
            createProfile(name, server, inPort, bypass);
        } else {
            updateProfile(name, server, inPort, bypass);
        }
    }

    @Override
    public void populateProfile() {
        if (isNewProfile()) {
            throw new RuntimeException("populateProfile() was called but profile is new.");
        }
        mProfilesDataSource.getProfile(mProfileId, new ProfilesDataSource.GetProfileCallback() {
            @Override
            public void onProfileLoaded(Profile profile) {
                if (!mAddProfileView.isActive()) return;

                mAddProfileView.setName(profile.getName());
                mAddProfileView.setServer(profile.getHost());
                mAddProfileView.setBypass(profile.getBypass());
                mAddProfileView.setInPort(String.valueOf(profile.getInPort()));

                mIsDataMissing = false;
            }

            @Override
            public void onDataNoAvailable() {
                if (!mAddProfileView.isActive()) return;
                mAddProfileView.showEmptyProfileError();
            }
        });
    }

    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    @Override
    public void onDestroy() {
        mProfilesDataSource.releaseResources();
    }

    private boolean isNewProfile() {
        return mProfileId == null;
    }

    private void createProfile(String name, String server,
                               String inPort, String bypass) {

        boolean isValidData = validateData(name, server,
                inPort, bypass);

        if (!isValidData) return;

        Profile profile = Profile.newProfile(name, server,
                Integer.parseInt(inPort), bypass);

        mProfilesDataSource.saveProfile(profile, new ProfilesDataSource.SaveProfileCallback() {
            @Override
            public void onProfileSaved() {
                mAddProfileView.finishAddEditProfileActivity();
            }

            @Override
            public void onProfileNameAlreadyExist() {
                if (mAddProfileView.isActive()) {
                    mAddProfileView.setProfileEqualNameError();
                }
            }
        });

    }

    private void updateProfile(String name, String server,
                               String inPort, String bypass) {

        boolean isValidData = validateData(name, server,
                inPort, bypass);

        if (!isValidData) return;

        Profile profile = Profile.newProfile(mProfileId, name, server,
                Integer.parseInt(inPort), bypass);

        mProfilesDataSource.updateProfile(profile, new ProfilesDataSource.UpdateProfileCallback() {
            @Override
            public void onProfileUpdated() {
                mAddProfileView.finishAddEditProfileActivity();
            }

            @Override
            public void onProfileNameAlreadyExist() {
                if (mAddProfileView.isActive()) {
                    mAddProfileView.setProfileEqualNameError();
                }
            }
        });
    }

    private boolean validateData(String name, String server,
                                 String inPort, String bypass) {
        boolean isValid = true;

        if (Strings.isNullOrEmpty(name)) {
            mAddProfileView.setNameEmptyError();
            isValid = false;
        }

        if (Strings.isNullOrEmpty(server)) {
            mAddProfileView.setServerEmptyError();
            isValid = false;
        }

        if (!InternetDomainName.isValid(server) && !InetAddresses.isInetAddress(server)){
            mAddProfileView.setServerInvalidError();
            isValid = false;
        }

        if (Strings.isNullOrEmpty(inPort)) {
            mAddProfileView.setInPortEmptyError();
            isValid = false;
        }

        if (!Strings.isNullOrEmpty(inPort) &&
                (Integer.parseInt(inPort) < 0 ||
                        Integer.parseInt(inPort) > MAX_PORTS_LIMIT)) {

            mAddProfileView.setInputPortOutOfRangeError();
            isValid = false;

        }

//        if (!Strings.isNullOrEmpty(domain) && !InternetDomainName.isValid(domain)){
//            mAddProfileView.setDomainInvalidError();
//            isValid = false;
//        }

        if (!isValidBypassSyntax(bypass)) {
            mAddProfileView.setBypassSyntaxError();
            isValid = false;
        }

        return isValid;
    }

    //TODO
    private boolean isValidBypassSyntax(String bypass) {
//        String validIpAddressRegex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
//        String validHostnameRegex = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";
//
//        StringBuilder regexSb = new StringBuilder();
//        Formatter formatter = new Formatter(regexSb, Locale.US);
//
//        String regex = "%1 | %2 | +([%1],%2) ";
//        regex = regex.replace("%1", validIpAddressRegex);
//        regex = regex.replace("%2", validHostnameRegex);
//
//        Pattern pattern = Pattern.compile(regex);
//        return pattern.matcher(bypass).find();
        return true;
    }
}
