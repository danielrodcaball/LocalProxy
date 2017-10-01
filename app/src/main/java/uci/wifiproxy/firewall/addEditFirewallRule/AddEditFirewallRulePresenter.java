package uci.wifiproxy.firewall.addEditFirewallRule;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;

import uci.wifiproxy.data.firewallRule.FirewallRule;
import uci.wifiproxy.data.firewallRule.FirewallRuleDataSource;
import uci.wifiproxy.data.firewallRule.FirewallRuleLocalDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 1/10/17.
 */

public class AddEditFirewallRulePresenter implements AddEditFirewallRuleContract.Presenter {

    @NonNull
    private FirewallRuleLocalDataSource mFirewallRuleDataSource;

    @NonNull
    private AddEditFirewallRuleContract.View mAddEditFirewallRuleView;

    @Nullable
    private String mFirewallRuleId;

    private boolean mIsDataMissing;

    public AddEditFirewallRulePresenter(@NonNull AddEditFirewallRuleContract.View addEditFirewallRuleView,
                                        @Nullable String firewallRuleId, boolean shouldLoadDataFromSource) {
        mFirewallRuleDataSource = FirewallRuleLocalDataSource.newInstance();
        mAddEditFirewallRuleView = checkNotNull(addEditFirewallRuleView);
        mFirewallRuleId = firewallRuleId;
        mIsDataMissing = shouldLoadDataFromSource;

        mAddEditFirewallRuleView.setPresenter(this);
    }


    @Override
    public void start() {
        if (!isNewFirewallRule() && mIsDataMissing)
            populateFirewallRule();
    }

    @Override
    public void saveFirewallRule(String rule, String description) {
        if (isNewFirewallRule())
            createFirewallRule(rule, description);
        else
            updateFirewallRule(rule, description);
    }

    @Override
    public void populateFirewallRule() {
        if (isNewFirewallRule()) {
            throw new RuntimeException("populateFirewallRule() was called but firewall rule is new.");
        }

        mFirewallRuleDataSource.getFirewallRule(mFirewallRuleId, new FirewallRuleDataSource.GetFirewallRuleCallback() {
            @Override
            public void onFirewallRuleLoaded(FirewallRule firewallRule) {
                if (!mAddEditFirewallRuleView.isActive()) return;

                mAddEditFirewallRuleView.setRule(firewallRule.getRule());
                mAddEditFirewallRuleView.setDescription(firewallRule.getDescription());

                mIsDataMissing = false;
            }

            @Override
            public void onDataNoAvailable() {
                if (!mAddEditFirewallRuleView.isActive()) return;
                mAddEditFirewallRuleView.setEmptyRuleError();
            }
        });
    }

    @Override
    public void onDestroy() {
        mFirewallRuleDataSource.releaseResources();
    }

    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    private boolean isNewFirewallRule() {
        return mFirewallRuleId == null;
    }

    private void createFirewallRule(String rule, String description) {
        boolean isValidData = validateData(rule, description);

        if (!isValidData) return;

        FirewallRule firewallRule = FirewallRule.newInstance(rule, description);

        mFirewallRuleDataSource.saveFirewallRule(firewallRule);

        if (mAddEditFirewallRuleView.isActive())
            mAddEditFirewallRuleView.finishAddEditFirewallRuleActivity();
    }

    private void updateFirewallRule(String rule, String description) {
        boolean isValidData = validateData(rule, description);

        if (!isValidData) return;

        FirewallRule firewallRule = FirewallRule.newInstance(mFirewallRuleId, rule, description);

        mFirewallRuleDataSource.updateFirewallRule(firewallRule);

        if (mAddEditFirewallRuleView.isActive())
            mAddEditFirewallRuleView.finishAddEditFirewallRuleActivity();
    }

    private boolean validateData(String rule, String description) {
        boolean isValid = true;

        if (Strings.isNullOrEmpty(rule)) {
            mAddEditFirewallRuleView.setEmptyRuleError();
            isValid = false;
        }

        return isValid;
    }
}
