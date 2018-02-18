package uci.wifiproxy.trace.tracesList;

import android.support.annotation.NonNull;

import java.util.List;

import uci.wifiproxy.BasePresenter;
import uci.wifiproxy.BaseView;
import uci.wifiproxy.data.trace.Trace;

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
