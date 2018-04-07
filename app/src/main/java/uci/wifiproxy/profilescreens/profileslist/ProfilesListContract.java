package uci.wifiproxy.profilescreens.profileslist;

import android.support.annotation.NonNull;

import java.util.List;

import uci.wifiproxy.BasePresenter;
import uci.wifiproxy.BaseView;
import uci.wifiproxy.data.profile.Profile;

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
