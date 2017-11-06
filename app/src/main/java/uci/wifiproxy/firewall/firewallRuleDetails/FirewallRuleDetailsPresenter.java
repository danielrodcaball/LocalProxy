package uci.wifiproxy.firewall.firewallRuleDetails;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;

import uci.wifiproxy.data.firewallRule.FirewallRule;
import uci.wifiproxy.data.firewallRule.FirewallRuleDataSource;
import uci.wifiproxy.data.firewallRule.FirewallRuleLocalDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 2/10/17.
 */

public class FirewallRuleDetailsPresenter implements FirewallRuleDetailsContract.Presenter {

    private FirewallRuleLocalDataSource mFirewallRuleDataSource;

    @NonNull
    private FirewallRuleDetailsContract.View mView;

    @Nullable
    private String mFirewallRuleId;

    public FirewallRuleDetailsPresenter(@NonNull FirewallRuleDetailsContract.View view,
                                        @Nullable String firewallRuleId) {
        mView = checkNotNull(view);
        mFirewallRuleId = firewallRuleId;
        mFirewallRuleDataSource = FirewallRuleLocalDataSource.newInstance();

        mView.setPresenter(this);
    }

    @Override
    public void start() {
        openFirewallRule();
    }

    private void openFirewallRule() {
        if (Strings.isNullOrEmpty(mFirewallRuleId)) {
            mView.showMissingFirewallRule();
            return;
        }

        mFirewallRuleDataSource.getFirewallRule(mFirewallRuleId, new FirewallRuleDataSource.GetFirewallRuleCallback() {
            @Override
            public void onFirewallRuleLoaded(FirewallRule firewallRule) {
                if (!mView.isActive()) return;

                mView.showRule(firewallRule.getRule());
                mView.showDescription(firewallRule.getDescription());
                mView.showPackageName(firewallRule.getApplicationPackageName());
                mView.showPackageLogo(firewallRule.getApplicationPackageName());
            }

            @Override
            public void onDataNoAvailable() {
                if (!mView.isActive()) return;

                mView.showMissingFirewallRule();
            }
        });
    }

    @Override
    public void editFirewallRule() {
        if (Strings.isNullOrEmpty(mFirewallRuleId)) {
            mView.showMissingFirewallRule();
            return;
        }
        mView.showEditFirewallRule(mFirewallRuleId);
    }

    @Override
    public void deleteFirewallRule() {
        if (Strings.isNullOrEmpty(mFirewallRuleId)) {
            mView.showMissingFirewallRule();
            return;
        }
        mFirewallRuleDataSource.deleteFirewallRule(mFirewallRuleId);
        mView.showFirewallRuleDeleted();
    }

    @Override
    public void result(int requestCode, int resultCode) {
        if (requestCode == FirewallRuleDetailsFragment.REQUEST_EDIT_FIREWALL_RULE
                && resultCode == Activity.RESULT_OK)
            mView.showSuccessfullyUpdatedMessage();
    }

    @Override
    public void onDestroy() {
        mFirewallRuleDataSource.releaseResources();
    }

}
