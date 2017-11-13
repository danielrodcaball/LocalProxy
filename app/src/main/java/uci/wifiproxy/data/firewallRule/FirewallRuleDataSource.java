package uci.wifiproxy.data.firewallRule;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by daniel on 29/09/17.
 */

public interface FirewallRuleDataSource {

    interface LoadFirewallRulesCallback{
        void onFirewallRulesLoaded(List<FirewallRule> firewallRules);
        void onDataNoAvailable();
    }

    interface GetFirewallRuleCallback{
        void onFirewallRuleLoaded(FirewallRule firewallRule);
        void onDataNoAvailable();
    }

    void getFirewallRules(@NonNull LoadFirewallRulesCallback callback);

    List<FirewallRule> getActiveFirewallRules();

    void getFirewallRule(@NonNull String id, @NonNull GetFirewallRuleCallback callback);

    void saveFirewallRule(@NonNull FirewallRule firewallRule);

    void updateFirewallRule(@NonNull FirewallRule firewallRule);

    void deleteFirewallRule(@NonNull String firewallRuleId);

    void deleteAllFirewallRules();

    void activateFirewallRule(@NonNull String firewallRuleId);

    void deactivateFirewallRule(@NonNull String firewallRuleId);

}
