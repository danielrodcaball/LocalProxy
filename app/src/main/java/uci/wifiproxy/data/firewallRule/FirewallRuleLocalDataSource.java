package uci.wifiproxy.data.firewallRule;

import android.support.annotation.NonNull;

import java.util.List;

import io.realm.Realm;

/**
 * Created by daniel on 29/09/17.
 */

public class FirewallRuleLocalDataSource implements FirewallRuleDataSource {

    private Realm realm;

    //Prevent direct instatntiation
    private FirewallRuleLocalDataSource() {
        realm = Realm.getDefaultInstance();
    }

    public static FirewallRuleLocalDataSource newInstance() {
        return new FirewallRuleLocalDataSource();
    }

    public void releaseResources() {
        if (realm != null) {
            realm.close();
        }
    }

    @Override
    public void getFirewallRules(@NonNull LoadFirewallRulesCallback callback) {
        List<FirewallRule> firewallRuleList = realm.where(FirewallRule.class).findAll();
        if (!firewallRuleList.isEmpty()) {
            callback.onFirewallRulesLoaded(firewallRuleList);
        } else {
            callback.onDataNoAvailable();
        }
    }

    @Override
    public void getActiveFirewallRules(@NonNull LoadFirewallRulesCallback callback) {
        List<FirewallRule> firewallRuleList = realm.where(FirewallRule.class)
                .equalTo(FirewallRule.IS_ACTIVE_FILED, true)
                .findAll();
        if (!firewallRuleList.isEmpty()) {
            callback.onFirewallRulesLoaded(firewallRuleList);
        } else {
            callback.onDataNoAvailable();
        }
    }

    @Override
    public void getFirewallRule(@NonNull String id, @NonNull GetFirewallRuleCallback callback) {
        FirewallRule firewallRule = realm.where(FirewallRule.class).equalTo(FirewallRule.ID_FILED, id).findFirst();
        if (firewallRule != null) {
            callback.onFirewallRuleLoaded(firewallRule);
        } else {
            callback.onDataNoAvailable();
        }
    }

    @Override
    public void saveFirewallRule(@NonNull FirewallRule firewallRule) {
        realm.beginTransaction();
        realm.copyToRealm(firewallRule);
        realm.commitTransaction();
    }

    @Override
    public void updateFirewallRule(@NonNull FirewallRule firewallRule) {
        FirewallRule firewallRuleToUpdate = realm.where(FirewallRule.class).equalTo(FirewallRule.ID_FILED, firewallRule.getId()).findFirst();

        if (firewallRuleToUpdate != null) {
            realm.beginTransaction();
            firewallRuleToUpdate.setRule(firewallRule.getRule());
            firewallRuleToUpdate.setDescription(firewallRule.getDescription());
            firewallRuleToUpdate.setApplicationPackageName(firewallRule.getApplicationPackageName());
            firewallRuleToUpdate.setActive(firewallRuleToUpdate.isActive());
            realm.commitTransaction();
        }
    }

    @Override
    public void deleteFirewallRule(@NonNull String firewallRuleId) {
        realm.beginTransaction();
        realm.where(FirewallRule.class).equalTo(FirewallRule.ID_FILED, firewallRuleId)
                .findFirst()
                .deleteFromRealm();
        realm.commitTransaction();
    }

    @Override
    public void deleteAllFirewallRules() {
        realm.beginTransaction();
        realm.where(FirewallRule.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    @Override
    public void activateFirewallRule(@NonNull String firewallRuleId) {
        activateDeactivateFirewallRule(firewallRuleId, true);
    }

    @Override
    public void deactivateFirewallRule(@NonNull String firewallRuleId) {
        activateDeactivateFirewallRule(firewallRuleId, false);
    }

    private void activateDeactivateFirewallRule(String firewallRuleId, boolean activate){
        FirewallRule firewallRule = realm.where(FirewallRule.class)
                .equalTo(FirewallRule.ID_FILED, firewallRuleId).findFirst();

        if (firewallRule != null){
            realm.beginTransaction();
            firewallRule.setActive(activate);
            realm.commitTransaction();
        }
    }


}
