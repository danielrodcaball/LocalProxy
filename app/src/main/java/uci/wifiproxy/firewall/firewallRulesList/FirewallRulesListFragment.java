package uci.wifiproxy.firewall.firewallRulesList;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uci.wifiproxy.R;
import uci.wifiproxy.data.firewallRule.FirewallRule;
import uci.wifiproxy.firewall.addEditFirewallRule.AddEditFirewallRuleActivity;

/**
 * Created by daniel on 29/09/17.
 */

public class FirewallRulesListFragment extends Fragment implements FirewallRulesListContract.View {

    private FirewallRulesListContract.Presenter mPresenter;

    private FirewallRulesAdapter mListAdapter;

    private View mNoFirewallRulesView;

    private LinearLayout mFirewallRulesView;

    FirewallRuleItemListener mItemListener = new FirewallRuleItemListener() {
        @Override
        public void onFirewallRuleClick(FirewallRule clickedFirewallRule) {
            //TODO
        }

        @Override
        public void onFirewallRuleCheckClick(FirewallRule checkedFirewallRule, boolean isChecked) {
            mPresenter.activateFirewallRule(checkedFirewallRule, isChecked);
        }

    };

    public FirewallRulesListFragment() {
        // Requires empty public constructor
    }

    public static FirewallRulesListFragment newInstance() {
        return new FirewallRulesListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new FirewallRulesAdapter(new ArrayList<FirewallRule>(0), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.firewall_rules_list_frag, container, false);

        //Set up FirewallRules view
        ListView listView = (ListView) root.findViewById(R.id.firewallRules_list);
        listView.setAdapter(mListAdapter);
        mFirewallRulesView = (LinearLayout) root.findViewById(R.id.firewallRulesLL);

        //Set up no FirewallRules view
        mNoFirewallRulesView = root.findViewById(R.id.noFirewallRules);
        TextView noFirewallRulesAddView = (TextView) root.findViewById(R.id.noFirewallRulesAdd);
        noFirewallRulesAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFirewallRule();
            }
        });

        //Set up floating action button
        FloatingActionButton fab = (FloatingActionButton) getActivity()
                .findViewById(R.id.fab_add_firewall_rule);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewFirewallRule();
            }
        });

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode);
    }

    @Override
    public void setPresenter(FirewallRulesListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showFirewallRules(List<FirewallRule> firewallRules) {
        mListAdapter.replaceData(firewallRules);

        mFirewallRulesView.setVisibility(View.VISIBLE);
        mNoFirewallRulesView.setVisibility(View.GONE);
    }

    @Override
    public void showAddFirewallRule() {
        Intent intent = new Intent(getContext(), AddEditFirewallRuleActivity.class);
        startActivityForResult(intent, AddEditFirewallRuleActivity.REQUEST_ADD_FIREWALL_RULE);
    }

    @Override
    public void showNoFirewallRules() {
        mFirewallRulesView.setVisibility(View.GONE);
        mNoFirewallRulesView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showFirewallRuleDetailsUI(String firewallRuleId) {
        //TODO
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_firewallRule_message));
    }

    @Override
    public void showFirewallRuleActivated() {
        showMessage(getString(R.string.firewall_rule_activated));
    }

    @Override
    public void showFirewallRuleDeactivate() {
        showMessage(getString(R.string.firewall_rule_deactivated));
    }

    private void showMessage(String message){
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }


    private static class FirewallRulesAdapter extends BaseAdapter {

        private List<FirewallRule> mFirewallRules;
        private FirewallRuleItemListener mItemListener;

        public FirewallRulesAdapter(List<FirewallRule> firewallRules, FirewallRuleItemListener itemListener) {
            this.mFirewallRules = firewallRules;
            this.mItemListener = itemListener;
        }

        private void setList(List<FirewallRule> firewallRules) {
            mFirewallRules = firewallRules;
        }

        public void replaceData(List<FirewallRule> firewallRules) {
            setList(firewallRules);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mFirewallRules.size();
        }

        @Override
        public FirewallRule getItem(int position) {
            return mFirewallRules.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.firewall_rule_list_item, parent, false);
            }

            final FirewallRule firewallRule = getItem(position);

            TextView ruleTv = (TextView) rowView.findViewById(R.id.rule);
            ruleTv.setText(firewallRule.getRule());

            CheckBox checked = (CheckBox) rowView.findViewById(R.id.active);

            checked.setChecked(firewallRule.isActive());
            if (!firewallRule.isActive()){
                rowView.setBackgroundDrawable(parent.getContext()
                        .getResources().getDrawable(R.drawable.list_deactivate_touch_feedback));
            }
            else{
                rowView.setBackgroundDrawable(parent.getContext()
                        .getResources().getDrawable(R.drawable.touch_feedback));
            }

            checked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onFirewallRuleCheckClick(firewallRule, !firewallRule.isActive());
                }
            });

            ruleTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onFirewallRuleClick(firewallRule);
                }
            });

            return rowView;
        }
    }

    public interface FirewallRuleItemListener {
        void onFirewallRuleClick(FirewallRule clickedFirewallRule);

        void onFirewallRuleCheckClick(FirewallRule checkedFirewallRule, boolean isChecked);

    }
}
