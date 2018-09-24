package uci.localproxy.firewallscreens.firewallruleslist;

import android.support.annotation.NonNull;

import java.util.List;

import uci.localproxy.BasePresenter;
import uci.localproxy.BaseView;
import uci.localproxy.data.firewallRule.FirewallRule;

/**
 * Created by daniel on 29/09/17.
 */

public interface FirewallRulesListContract {

    interface Presenter extends BasePresenter{

        void result(int requestCode, int resultCode);

        void loadFirewallRules();

        void addNewFirewallRule();

        void openFirewallRuleDetails(@NonNull FirewallRule requestedFirewallRule);

        void onDestroy();

        void activateFirewallRule(@NonNull FirewallRule requestedFirewallRule, @NonNull boolean activate);
    }

    interface View extends BaseView<Presenter>{

        void showFirewallRules(List<FirewallRule> firewallRules);

        void showAddFirewallRule();

        void showNoFirewallRules();

        void showFirewallRuleDetailsUI(String firewallRuleId);

        void showSuccessfullySavedMessage();

        void showFirewallRuleActivated();

        void showFirewallRuleDeactivate();

        boolean isActive();
    }
}
