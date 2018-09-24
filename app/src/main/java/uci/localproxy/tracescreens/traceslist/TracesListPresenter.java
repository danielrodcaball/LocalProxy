package uci.localproxy.tracescreens.traceslist;

import android.support.annotation.NonNull;

import java.util.List;

import uci.localproxy.data.firewallRule.FirewallRule;
import uci.localproxy.data.firewallRule.FirewallRuleLocalDataSource;
import uci.localproxy.data.trace.Trace;
import uci.localproxy.data.trace.TraceDataSource;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 16/02/18.
 */

public class TracesListPresenter implements TracesListContract.Presenter {

    private TraceDataSource mTraceDataSource;
    private FirewallRuleLocalDataSource mFirewallRuleDataSource;
    private TracesListContract.View mView;

    public TracesListPresenter(@NonNull TracesListContract.View view){
        mTraceDataSource = TraceDataSource.newInstance();
        mFirewallRuleDataSource = FirewallRuleLocalDataSource.newInstance();
        mView = checkNotNull(view, "view cannot be null");

        mView.setPresenter(this);
    }

    @Override
    public void addAsFirewallRule(String rule, String appPackageName) {
        FirewallRule firewallRule = FirewallRule.newInstance(rule, appPackageName, "Imported from traces");
        mFirewallRuleDataSource.saveFirewallRule(firewallRule);
        mView.showSuccessfullyAddedAsFirewallRuleMessage();
    }

    @Override
    public void loadTraces(String filter, boolean sortByConsumption) {
        loadTraces(filter, sortByConsumption, true);
    }

    @Override
    public void deleteAllTraces() {
        mTraceDataSource.deleteAllTraces();
        mView.showNoTraces();
    }

    private void loadTraces(String filter, boolean sortByConsumption, final boolean showLoadingUi){
        if (showLoadingUi){
            mView.setLoadingIndicator(true);
        }

        mTraceDataSource.filterTraces(filter, sortByConsumption, new TraceDataSource.LoadTracesCallback() {
            @Override
            public void onTracesLoaded(List<Trace> traces) {
                if (!mView.isActive()) return;

                mView.showTraces(traces);

                if (showLoadingUi){
                    mView.setLoadingIndicator(false);
                }
            }

            @Override
            public void onDataNoAvailable() {
                if (!mView.isActive()) return;
                mView.showNoTraces();
                mView.setLoadingIndicator(false);
            }
        });

    }

    @Override
    public void openTracesDetails(@NonNull Trace requestedTrace) {

    }

    @Override
    public void onDestroy() {
        mTraceDataSource.releaseResources();
        mFirewallRuleDataSource.releaseResources();
    }

    @Override
    public void start() {
        loadTraces(null, false);
    }
}
