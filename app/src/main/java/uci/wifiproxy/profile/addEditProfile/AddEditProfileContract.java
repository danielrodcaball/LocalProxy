package uci.wifiproxy.profile.addEditProfile;

import uci.wifiproxy.BasePresenter;
import uci.wifiproxy.BaseView;

/**
 * Created by daniel on 17/09/17.
 */

public interface AddEditProfileContract {

    interface View extends BaseView<Presenter> {

        void showEmptyProfileError();

        void finishAddEditProfileActivity();

        void setName(String name);

        void setServer(String server);

        void setInPort(String inPort);

        void setBypass(String bypass);

        boolean isActive();

        void setProfileEqualNameError();

        void setNameEmptyError();

        void setServerEmptyError();

        void setInPortEmptyError();

        void setInputPortOutOfRangeError();

        void setBypassSyntaxError();

    }

    interface Presenter extends BasePresenter{

        void saveProfile(String name, String server,
                         String inPort, String bypass);

        void populateProfile();

        boolean isDataMissing();

        void onDestroy();

    }
}
