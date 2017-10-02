package uci.wifiproxy.firewall.firewallRuleDetails;

import uci.wifiproxy.BasePresenter;
import uci.wifiproxy.BaseView;

/**
 * Created by daniel on 2/10/17.
 */

public interface FirewallRuleDetailsContract {

    interface Presenter extends BasePresenter{

        void editFirewallRule();

        void deleteFirewallRule();

        void result(int requestCode, int resultCode);

        void onDestroy();
    }

    interface View extends BaseView<Presenter>{

        void showMissingFirewallRule();

        void showRule(String rule);

        void showDescription(String description);

        void showFirewallRuleDeleted();

        void showSuccessfullyUpdatedMessage();

        void showEditFirewallRule(String firewallRuleId);

        boolean isActive();
    }
}
