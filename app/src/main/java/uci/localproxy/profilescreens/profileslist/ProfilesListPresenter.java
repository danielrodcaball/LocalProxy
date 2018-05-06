package uci.localproxy.profilescreens.profileslist;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.List;

import uci.localproxy.data.profile.Profile;
import uci.localproxy.data.profile.source.ProfilesDataSource;
import uci.localproxy.data.profile.source.ProfilesLocalDataSource;
import uci.localproxy.profilescreens.addeditprofile.AddEditProfileActivity;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 16/09/17.
 */

public class ProfilesListPresenter implements ProfilesListContract.Presenter {

    private final ProfilesLocalDataSource mProfileDataSource;

    private final ProfilesListContract.View mProfilesView;

    public ProfilesListPresenter(@NonNull ProfilesListContract.View profilesView){
        mProfileDataSource = ProfilesLocalDataSource.newInstance();
        mProfilesView = checkNotNull(profilesView, "profilesView cannot be null");

        mProfilesView.setPresenter(this);
    }


    @Override
    public void start() {
        loadProfiles();
    }

    @Override
    public void result(int requestCode, int resultCode) {
        // If a profile was successfully added, show snackbar
        if (AddEditProfileActivity.REQUEST_ADD_PROFILE == requestCode && Activity.RESULT_OK == resultCode) {
            mProfilesView.showSuccessfullySavedMessage();
        }
    }

    @Override
    public void loadProfiles() {
        loadProfiles(false);
    }

    private void loadProfiles(final boolean showLoadingUi){
        if (showLoadingUi){
            mProfilesView.setLoadingIndicator(true);
        }
        mProfileDataSource.getProfiles(new ProfilesDataSource.LoadProfilesCallback() {
            @Override
            public void onProfilesLoaded(List<Profile> profiles) {
                if (!mProfilesView.isActive()) return;

                mProfilesView.showProfiles(profiles);

                if (showLoadingUi){
                    mProfilesView.setLoadingIndicator(false);
                }
            }

            @Override
            public void onDataNoAvailable() {
                if (!mProfilesView.isActive()) return;
                mProfilesView.showNoProfiles();
            }
        });
    }

    @Override
    public void addNewProfile() {
        mProfilesView.showAddProfile();
    }

    @Override
    public void openProfileDetails(@NonNull Profile requestedProfile) {
        mProfilesView.showProfileDetailsUI(requestedProfile.getId());
    }

    @Override
    public void onDestroy() {
        mProfileDataSource.releaseResources();
    }

}
