package uci.wifiproxy.tracescreens.tracedetails;

import uci.wifiproxy.BasePresenter;
import uci.wifiproxy.BaseView;

/**
 * Created by daniel on 17/02/18.
 */

public interface TraceDetailsContract {

    interface Presenter extends BasePresenter{

        void addAsFirewallRule(String rule, String appPackageName);

        void result(int requestCode, int resultCode);

        void onDestroy();
    }

    interface View extends BaseView<Presenter>{

        void showPackageLogo(String packageName);

        void showApplicationName(String applicationName);

        void showRequestedUrl(String requestedUrl);

        void showConsumption(String consumption);

        void showSuccessfullyAddedAsFirewallRuleMessage();

        boolean isActive();
    }
}
