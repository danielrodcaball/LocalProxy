package uci.wifiproxy.firewall.firewallRulesList;

import android.support.annotation.NonNull;

import java.util.List;

import uci.wifiproxy.data.firewallRule.FirewallRule;
import uci.wifiproxy.data.firewallRule.FirewallRuleDataSource;
import uci.wifiproxy.data.firewallRule.FirewallRuleLocalDataSource;

/**
 * Created by daniel on 29/09/17.
 */

public class FirewallRulesListPresenter implements FirewallRulesListContract.Presenter {

    private final FirewallRuleLocalDataSource mFirewallRulesDataSource;

    private final FirewallRulesListContract.View mFirewallRulesView;

    public FirewallRulesListPresenter(@NonNull FirewallRulesListContract.View firewallRulesView){
        mFirewallRulesDataSource = FirewallRuleLocalDataSource.newInstance();
        mFirewallRulesView = firewallRulesView;

        mFirewallRulesView.setPresenter(this);
    }


    @Override
    public void start() {
        loadFirewallRules();
    }

    @Override
    public void result(int requestCode, int resultCode) {
        // If a profile was successfully added, show snackbar
        //TODO
    }

    @Override
    public void loadFirewallRules() {
        mFirewallRulesDataSource.getFirewallRules(new FirewallRuleDataSource.LoadFirewallRulesCallback() {
            @Override
            public void onFirewallRulesLoaded(List<FirewallRule> firewallRules) {
                if (!mFirewallRulesView.isActive()) return;

                mFirewallRulesView.showFirewallRules(firewallRules);
            }

            @Override
            public void onDataNoAvailable() {
                if (!mFirewallRulesView.isActive()) return;

                mFirewallRulesView.showNoFirewallRules();
            }
        });
    }

    @Override
    public void addNewFirewallRule() {
        mFirewallRulesView.showAddFirewallRule();
    }

    @Override
    public void openFirewallRuleDetails(@NonNull FirewallRule requestedFirewallRule) {
        mFirewallRulesView.showFirewallRuleDetailsUI(requestedFirewallRule.getId());
    }

    @Override
    public void onDestroy() {
        mFirewallRulesDataSource.releaseResources();
    }

}
