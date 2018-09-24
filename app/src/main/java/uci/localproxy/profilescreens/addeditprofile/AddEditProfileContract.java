package uci.localproxy.profilescreens.addeditprofile;

import uci.localproxy.BasePresenter;
import uci.localproxy.BaseView;

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

//        void setDomain(String domain);

        boolean isActive();

        void setProfileEqualNameError();

        void setNameEmptyError();

        void setServerEmptyError();

        void setServerInvalidError();

        void setInPortEmptyError();

        void setInputPortOutOfRangeError();

        void setBypassSyntaxError();

//        void setDomainInvalidError();

    }

    interface Presenter extends BasePresenter{

        void saveProfile(String name, String server,
                         String inPort, String bypass);

        void populateProfile();

        boolean isDataMissing();

        void onDestroy();

    }
}
