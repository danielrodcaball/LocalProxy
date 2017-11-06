package uci.wifiproxy.firewall.firewallRuleDetails;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import uci.wifiproxy.R;
import uci.wifiproxy.firewall.addEditFirewallRule.AddEditFirewallRuleActivity;
import uci.wifiproxy.firewall.addEditFirewallRule.AddEditFirewallRuleFragment;

/**
 * Created by daniel on 2/10/17.
 */

public class FirewallRuleDetailsFragment extends Fragment implements FirewallRuleDetailsContract.View {

    @NonNull
    public static final String ARGUMENT_FIREWALL_RULE_ID = "FIREWALL_RULE_ID";

    @NonNull
    public static final int REQUEST_EDIT_FIREWALL_RULE = 1;

    private FirewallRuleDetailsContract.Presenter mPresenter;

    private ImageView mPackageLogoTv;

    private TextView mPackageNameTv;

    private TextView mRuleTv;

    private TextView mDescriptionTv;

    public FirewallRuleDetailsFragment(){
        //Requires an empty public constructor
    }

    public static FirewallRuleDetailsFragment newInstance(){
        return new FirewallRuleDetailsFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }


    @Override
    public void setPresenter(FirewallRuleDetailsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.firewallrule_details_frag, container, false);
        mRuleTv = (TextView) root.findViewById(R.id.firewallrule_detail_rule);
        mDescriptionTv = (TextView) root.findViewById(R.id.firewallrule_detail_description);
        mPackageNameTv = (TextView) root.findViewById(R.id.packageName);
        mPackageLogoTv = (ImageView) root.findViewById(R.id.packageLogo);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_firewallrule);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.editFirewallRule();
            }
        });

        setHasOptionsMenu(true);

        return root;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.firewallruledetail_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_delete:
                mPresenter.deleteFirewallRule();
                break;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void showMissingFirewallRule() {
        showMessage(getString(R.string.missing_data_message));
    }

    @Override
    public void showRule(String rule) {
        mRuleTv.setText(rule);
    }

    @Override
    public void showDescription(String description) {
        mDescriptionTv.setText(description);
    }

    @Override
    public void showFirewallRuleDeleted() {
        getActivity().finish();
    }

    @Override
    public void showPackageName(String packageName) {
        mPackageNameTv.setText(packageName);
    }

    @Override
    public void showPackageLogo(String packageName) {
        try {
            mPackageLogoTv.setImageDrawable(getPackageLogoDrawable(packageName, getContext()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Drawable getPackageLogoDrawable(String packageName, Context context) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        Drawable drawable = pm.getApplicationIcon   (packageName);
        return drawable;
    }

    @Override
    public void showSuccessfullyUpdatedMessage() {
        showMessage(getString(R.string.successfully_firewallrule_updated_message));
    }

    @Override
    public void showEditFirewallRule(String firewallRuleId) {
        Intent intent = new Intent(getContext(), AddEditFirewallRuleActivity.class);
        intent.putExtra(AddEditFirewallRuleFragment.ARGUMENT_EDIT_FIREWALL_RULE_ID, firewallRuleId);
        startActivityForResult(intent, REQUEST_EDIT_FIREWALL_RULE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    private void showMessage(String message){
        Snackbar.make(mRuleTv, message, Snackbar.LENGTH_LONG).show();
    }

}
