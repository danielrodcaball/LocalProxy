package uci.localproxy.headerscreen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import uci.localproxy.BasePresenter;
import uci.localproxy.BaseView;
import uci.localproxy.data.firewallRule.FirewallRule;
import uci.localproxy.data.header.Header;

/**
 * Created by daniel on 30/08/18.
 */

public interface HeaderListContract {

    interface Presenter extends BasePresenter{

        void loadHeaders();

        void addNewHeader();

        void editHeader(@NonNull Header requestedHeader);

        void saveHeader(@NonNull Header header);

        void removeHeader(@NonNull String headerId);

        void onDestroy();
    }

    interface View extends BaseView<Presenter>{

        void showHeaders(List<Header> headers);

        void showAddEditHeaderDialog(@Nullable Header header);

        void showNoHeaders();

        void showSuccessfullySavedMessage();

        void showHeaderNameAlreadyExistError();

        void showHeaderRemovedMessage();

        boolean isActive();
    }
}
