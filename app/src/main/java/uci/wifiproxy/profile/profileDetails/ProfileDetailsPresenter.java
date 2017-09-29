package uci.wifiproxy.profile.profileDetails;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;

import uci.wifiproxy.data.profile.Profile;
import uci.wifiproxy.data.profile.source.ProfilesDataSource;
import uci.wifiproxy.data.profile.source.ProfilesLocalDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 18/09/17.
 */

public class ProfileDetailsPresenter implements ProfileDetailsContract.Presenter{

    private final ProfilesLocalDataSource mProfilesLocalDataSource;

    private final ProfileDetailsContract.View mProfilesDetailsView;

    @Nullable
    private String mProfileId;

    public ProfileDetailsPresenter(ProfileDetailsContract.View profilesDetailsView, String profileId) {
        mProfilesLocalDataSource = ProfilesLocalDataSource.newInstance();
        mProfilesDetailsView = checkNotNull(profilesDetailsView);
        mProfileId = profileId;

        mProfilesDetailsView.setPresenter(this);
    }


    @Override
    public void start() {
        openProfile();
    }

    private void openProfile(){
        if (Strings.isNullOrEmpty(mProfileId)){
            mProfilesDetailsView.showMissingProfile();
            return;
        }

        mProfilesLocalDataSource.getProfile(mProfileId, new ProfilesDataSource.GetProfileCallback() {
            @Override
            public void onProfileLoaded(Profile profile) {
                if (!mProfilesDetailsView.isActive()){
                    return;
                }
                showProfile(profile);
            }

            @Override
            public void onDataNoAvailable() {
                if (!mProfilesDetailsView.isActive()){
                    return;
                }
                mProfilesDetailsView.showMissingProfile();
            }
        });
    }

    private void showProfile(@NonNull Profile profile){
        if (!mProfilesDetailsView.isActive()){
            return;
        }

        mProfilesDetailsView.showName(profile.getName());
        mProfilesDetailsView.showServer(profile.getServer());
        mProfilesDetailsView.showInPort(profile.getInPort());
        mProfilesDetailsView.showOutPort(profile.getOutPort());
        mProfilesDetailsView.showBypass(profile.getBypass());
    }

    @Override
    public void editProfile() {
        if (Strings.isNullOrEmpty(mProfileId)){
            mProfilesDetailsView.showMissingProfile();
            return;
        }
        mProfilesDetailsView.showEditProfile(mProfileId);
    }

    @Override
    public void deleteProfile() {
        if (Strings.isNullOrEmpty(mProfileId)){
            mProfilesDetailsView.showMissingProfile();
            return;
        }
        mProfilesLocalDataSource.deleteProfile(mProfileId);
        mProfilesDetailsView.showProfileDeleted();
    }

    @Override
    public void result(int requestCode, int resultCode) {
        if (requestCode == ProfileDetailsFragment.REQUEST_EDIT_PROFILE &&
                resultCode == Activity.RESULT_OK){
            mProfilesDetailsView.showSuccessfullyUpdatedMessage();
        }
    }

    @Override
    public void onDestroy() {
        mProfilesLocalDataSource.releaseResources();
    }

}
