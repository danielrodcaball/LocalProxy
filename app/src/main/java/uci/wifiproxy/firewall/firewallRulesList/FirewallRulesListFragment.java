package uci.wifiproxy.firewall.firewallRulesList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import uci.wifiproxy.data.firewallRule.FirewallRule;
import uci.wifiproxy.profile.profilesList.ProfilesListFragment;

/**
 * Created by daniel on 29/09/17.
 */

public class FirewallRulesListFragment implements FirewallRulesListContract.View {

    private FirewallRulesListContract.Presenter mPresenter;

    public FirewallRulesListFragment(){
        // Requires empty public constructor
    }

    public static FirewallRulesListFragment newInstance() {
        return new FirewallRulesListFragment();
    }

    @Override
    public void showFirewallRules(List<FirewallRule> firewallRules) {

    }

    @Override
    public void showAddFirewallRule() {

    }

    @Override
    public void showNoFirewallRules() {

    }

    @Override
    public void showFirewallRuleDetailsUI(String firewallRuleId) {

    }

    @Override
    public void showSuccessfullySavedMessage() {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setPresenter(FirewallRulesListContract.Presenter presenter) {

    }

    private static class FirewallRulesAdapter extends BaseAdapter {

        private List<FirewallRule> mFirewallRules;
        private FirewallRuleItemListener mItemListener;

        public FirewallRulesAdapter(List<FirewallRule> firewallRules, FirewallRuleItemListener itemListener) {
            this.mFirewallRules = firewallRules;
            this.mItemListener = itemListener;
        }

        private void setList(List<FirewallRule> firewallRules){
            mFirewallRules = firewallRules;
        }

        public void replaceData(List<FirewallRule> firewallRules){
            setList(firewallRules);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mFirewallRules.size();
        }

        @Override
        public Object getItem(int position) {
            return mFirewallRules.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (convertView == null){
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate()
            }
        }
    }

    public interface FirewallRuleItemListener {
        void onFirewallRuleClick(FirewallRule clickedFirewallRule);
    }
}
