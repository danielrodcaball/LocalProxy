package uci.wifiproxy.firewall.addEditFirewallRule;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uci.wifiproxy.R;

/**
 * Created by daniel on 1/10/17.
 */

public class AddEditFirewallRuleFragment extends Fragment implements AddEditFirewallRuleContract.View {

    public static final String ARGUMENT_EDIT_FIREWALL_RULE_ID = "EDIT_FIREWALL_RULE_ID";

    private AddEditFirewallRuleContract.Presenter mPresenter;

    private TextView mRule;

    private TextView mDescription;

    public AddEditFirewallRuleFragment(){
        // Required empty public constructor
    }

    public static AddEditFirewallRuleFragment newInstance(){
        return new AddEditFirewallRuleFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_firewallRule_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.saveFirewallRule(mRule.getText().toString(), mDescription.getText().toString());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.addfirewallrule_frag, container, false);
        mRule = (TextView) root.findViewById(R.id.add_firewallRule_rule);
        mDescription = (TextView) root.findViewById(R.id.add_firewallRule_description);

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setPresenter(AddEditFirewallRuleContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showEmptyFirewallRuleError() {
        Snackbar.make(mRule, getString(R.string.empty_firewallRule_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void finishAddEditFirewallRuleActivity() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setRule(String rule) {
        mRule.setText(rule);
    }

    @Override
    public void setDescription(String description) {
        mDescription.setText(description);
    }

    @Override
    public void setEmptyRuleError() {
        mRule.setError(getString(R.string.empty_rule_message));
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

}
