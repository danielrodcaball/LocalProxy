package uci.wifiproxy.proxy;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import java.util.List;

import uci.wifiproxy.BasePresenter;
import uci.wifiproxy.BaseView;
import uci.wifiproxy.data.profile.Profile;
import uci.wifiproxy.data.user.User;
import uci.wifiproxy.profile.AuthScheme;

/**
 * Created by daniel on 15/09/17.
 */

public interface ProxyContract {

    interface Presenter extends BasePresenter{

        void startProxy(@NonNull String user, @NonNull String pass, @NonNull String profileID,
                        @NonNull boolean rememberPass,@NonNull boolean setGlobalProxy);

        void stopProxy();

        void onDestroy();

        void filterUsers(String usernameText);

        void onTouchButtonViewPass(@NonNull int action);

        void addNewProfile();
    }

    interface View extends BaseView<Presenter>{

        void enableAllViews();

        void disableAllViews();

        void setPlayView();

        void setStopView();

        void setUsername(String username);

        void setPassword(String password);

        void setRememberPassword(boolean remember);

        void setGlobalProxyChecked(boolean checked);

        void setSpinnerProfiles(List<Profile> profiles);

        void setSpinnerProfileSelected(String profileId);

        void showNoProfilesView();

        void setUsernameEmptyError();

        void setPasswordEmptyError();

        void setProfileNoSelectedError();

        void setUsers(@NonNull List<User> users);

        void setPasswordVisibility(boolean visibility);

        void showAddProfile();

    }
}
