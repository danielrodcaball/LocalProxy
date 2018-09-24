package uci.localproxy.tracescreens.traceslist;

import android.support.annotation.NonNull;

import java.util.List;

import uci.localproxy.BasePresenter;
import uci.localproxy.BaseView;
import uci.localproxy.data.trace.Trace;

/**
 * Created by daniel on 16/02/18.
 */

public interface TracesListContract {

    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);
        void showTraces(List<Trace> traces);
        void showSuccessfullyAddedAsFirewallRuleMessage();
        void showNoTraces();
        boolean isActive();
    }

    interface Presenter extends BasePresenter{
        void addAsFirewallRule(String rule, String appPackageName);
        void loadTraces(String filter, boolean sortByConsumption);
        void deleteAllTraces();
        void openTracesDetails(@NonNull Trace requestedTrace);
        void onDestroy();
    }

}
