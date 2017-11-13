package uci.wifiproxy.data.firewallRule;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by daniel on 29/09/17.
 */

public class FirewallRule extends RealmObject {

    public final static String ID_FILED = "id";

    public final static String RULE_FILED = "rule";

    public final static String APPLICATION_PACKAGE_NAME_FIELD = "applicationPackageName";

    public final static String IS_ACTIVE_FILED = "isActive";


    @PrimaryKey
    private String id;

    @Required
    private String rule;

    @Required
    private String applicationPackageName;

    private String description;

    private boolean isActive;

    public static FirewallRule newInstance(@NonNull String firewallRuleId, @NonNull String rule,
                                           @NonNull String applicationPackageName,
                                           @Nullable String description){
        FirewallRule firewallRule = new FirewallRule();

        firewallRule.setId(firewallRuleId);
        firewallRule.setApplicationPackageName(applicationPackageName);
        firewallRule.setRule(rule);
        firewallRule.setDescription(description);
        firewallRule.setActive(true);

        return firewallRule;
    }

    public static FirewallRule newInstance(@NonNull String rule,
                                           @NonNull String applicationPackageName,
                                           @Nullable String description){
        FirewallRule firewallRule = new FirewallRule();

        firewallRule.setId(UUID.randomUUID().toString());
        firewallRule.setApplicationPackageName(applicationPackageName);
        firewallRule.setRule(rule);
        firewallRule.setDescription(description);
        firewallRule.setActive(true);

        return firewallRule;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApplicationPackageName() {
        return applicationPackageName;
    }

    public void setApplicationPackageName(String applicationPackageName) {
        this.applicationPackageName = applicationPackageName;
    }

    public FirewallRuleLoaded getFirewallRuleLoaded(){
        return new FirewallRuleLoaded(id, applicationPackageName, rule, description, isActive);
    }


    public class FirewallRuleLoaded{
        public String id;
        public String applicationPackageName;
        public String rule;
        public String description;
        public boolean isActive;

        public FirewallRuleLoaded(String id, String applicationPackageName, String rule, String description, boolean isActive) {
            this.id = id;
            this.applicationPackageName = applicationPackageName;
            this.rule = rule;
            this.description = description;
            this.isActive = isActive;
        }
    }
}
