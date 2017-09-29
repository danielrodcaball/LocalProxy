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

    public final static String IS_CHECKED_FILED = "isChecked";


    @PrimaryKey
    private String id;

    @Required
    private String rule;

    private String description;

    @Required
    private boolean isChecked;

    public static FirewallRule newInstance(@NonNull String firewallRuleId, @NonNull String rule,
                                           @Nullable String description){
        FirewallRule firewallRule = new FirewallRule();

        firewallRule.setId(firewallRuleId);
        firewallRule.setRule(rule);
        firewallRule.setDescription(description);
        firewallRule.setChecked(true);

        return firewallRule;
    }

    public static FirewallRule newInstance(@NonNull String rule, @NonNull String description){
        FirewallRule firewallRule = new FirewallRule();

        firewallRule.setId(UUID.randomUUID().toString());
        firewallRule.setRule(rule);
        firewallRule.setDescription(description);
        firewallRule.setChecked(true);

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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
