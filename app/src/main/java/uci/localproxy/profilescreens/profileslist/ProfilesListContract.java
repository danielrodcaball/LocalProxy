package uci.localproxy.profilescreens.profileslist;

import android.support.annotation.NonNull;

import java.util.List;

import uci.localproxy.BasePresenter;
import uci.localproxy.BaseView;
import uci.localproxy.data.profile.Profile;

/**
 * Created by daniel on 16/09/17.
 */

public interface ProfilesListContract {

    interface Presenter extends BasePresenter{

        void result(int requestCode, int resultCode);

        void loadProfiles();

        void addNewProfile();

        void openProfileDetails(@NonNull Profile requestedProfile);

        void onDestroy();
    }

    interface View extends BaseView<Presenter>{

        void setLoadingIndicator(boolean active);

        void showProfiles(List<Profile> profiles);

        void showAddProfile();

        void showNoProfiles();

        void showProfileDetailsUI(String profileId);

        void showSuccessfullySavedMessage();

        boolean isActive();
    }
}
