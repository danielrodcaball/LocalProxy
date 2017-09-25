package uci.wifiproxy.profile.addEditProfile;

import android.support.annotation.NonNull;

import uci.wifiproxy.BasePresenter;
import uci.wifiproxy.BaseView;
import uci.wifiproxy.profile.AuthScheme;

/**
 * Created by daniel on 17/09/17.
 */

public interface AddEditProfileContract {

    interface View extends BaseView<Presenter> {

        void showEmptyProfileError();

        void finishAddEditProfileActivity();

        void setName(String name);

        void setAuthScheme(AuthScheme authScheme);

        void setDomain(String domain);

        void setServer(String server);

        void setInPort(String inPort);

        void setOutPort(String outPort);

        void setBypass(String bypass);

        boolean isActive();

        void setProfileEqualNameError();

        void setNameEmptyError();

        void setDomainEmptyError();

        void setServerEmptyError();

        void setInPortEmptyError();

        void setOutPortEmptyError();

        void setBypassSyntaxError();

        void showDomainEntry();

        void hideDomainEntry();
    }

    interface Presenter extends BasePresenter{

        void saveProfile(String name, AuthScheme authScheme, String domain, String server,
                         String inPort, String outPort, String bypass);

        void populateProfile();

        boolean isDataMissing();

        void onDestroy();

        void onAuthSchemeSpinnerItemSelected(@NonNull int position);
    }
}
