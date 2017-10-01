package uci.wifiproxy.firewall.addEditFirewallRule;

import uci.wifiproxy.BasePresenter;
import uci.wifiproxy.BaseView;

/**
 * Created by daniel on 1/10/17.
 */

public interface AddEditFirewallRuleContract {

    interface View extends BaseView<Presenter>{

        void showEmptyFirewallRuleError();

        void finishAddEditFirewallRuleActivity();

        void setRule(String rule);

        void setDescription(String description);

        void setEmptyRuleError();

        //check if the view is active
        boolean isActive();
    }

    interface Presenter extends BasePresenter{

        void saveFirewallRule(String rule, String description);

        void populateFirewallRule();

        void onDestroy();

        boolean isDataMissing();
    }
}
