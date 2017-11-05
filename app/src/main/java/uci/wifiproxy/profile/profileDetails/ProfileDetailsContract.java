package uci.wifiproxy.profile.profileDetails;

import uci.wifiproxy.BasePresenter;
import uci.wifiproxy.BaseView;

/**
 * Created by daniel on 18/09/17.
 */

public interface ProfileDetailsContract {

    interface Presenter extends BasePresenter{

        void editProfile();

        void deleteProfile();

        void result(int requestCode, int resultCode);

        void onDestroy();

    }

    interface View extends BaseView<Presenter>{

        void showMissingProfile();

        void showName(String name);

        void showServer(String server);

        void showInPort(int inPort);

        void showBypass(String bypass);

        void showEditProfile(String profileId);

        void showProfileDeleted();

        void showSuccessfullyUpdatedMessage();

        boolean isActive();
    }
}
