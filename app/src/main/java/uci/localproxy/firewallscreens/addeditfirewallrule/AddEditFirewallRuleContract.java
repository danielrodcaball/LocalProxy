package uci.localproxy.firewallscreens.addeditfirewallrule;

import java.util.List;

import uci.localproxy.BasePresenter;
import uci.localproxy.BaseView;
import uci.localproxy.data.applicationPackage.ApplicationPackage;

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

        void setApplicationPackages(List<ApplicationPackage> applicationPackages);

        void setSpinnerApplicationPackageSelected(String packageName);

        //check if the view is active
        boolean isActive();
    }

    interface Presenter extends BasePresenter{

        void saveFirewallRule(String rule, String packageName, String description);

        void populateFirewallRule();

        void onDestroy();

        boolean isDataMissing();
    }
}
