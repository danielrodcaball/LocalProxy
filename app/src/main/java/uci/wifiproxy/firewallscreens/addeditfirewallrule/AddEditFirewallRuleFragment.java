package uci.wifiproxy.firewallscreens.addeditfirewallrule;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uci.wifiproxy.R;
import uci.wifiproxy.data.applicationPackage.ApplicationPackage;
import uci.wifiproxy.data.applicationPackage.ApplicationPackageLocalDataSource;

/**
 * Created by daniel on 1/10/17.
 */

public class AddEditFirewallRuleFragment extends Fragment implements AddEditFirewallRuleContract.View {

    public static final String ARGUMENT_EDIT_FIREWALL_RULE_ID = "EDIT_FIREWALL_RULE_ID";

    private AddEditFirewallRuleContract.Presenter mPresenter;

    private TextView mRule;

    private Spinner mPackageNameSpinner;

    private ApplicationPackagesAdapter mApplicationPackageAdapter;

    private TextView mDescription;

    public AddEditFirewallRuleFragment(){
        // Required empty public constructor
    }

    public static AddEditFirewallRuleFragment newInstance(){
        return new AddEditFirewallRuleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplicationPackageAdapter = new ApplicationPackagesAdapter(getContext(),
                new ArrayList<ApplicationPackage>(0));
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_firewallRule_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.saveFirewallRule(mRule.getText().toString(),
                        ((ApplicationPackage)mPackageNameSpinner.getSelectedItem()).getPackageName(),
                        mDescription.getText().toString());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root  = inflater.inflate(R.layout.addfirewallrule_frag, container, false);
        mRule = root.findViewById(R.id.add_firewallRule_rule);
        mDescription = root.findViewById(R.id.add_firewallRule_description);
        mPackageNameSpinner = root.findViewById(R.id.applicationSpinner);
        mPackageNameSpinner.setAdapter(mApplicationPackageAdapter);

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
    public void setApplicationPackages(List<ApplicationPackage> applicationPackages) {
        mApplicationPackageAdapter.replaceData(applicationPackages);
    }

    @Override
    public void setSpinnerApplicationPackageSelected(String packageName) {
        int pos = -1;
        for (int i = 0; i < mApplicationPackageAdapter.getCount(); i++){
            if (packageName.equals(((ApplicationPackage)mApplicationPackageAdapter.getItem(i)).getPackageName())){
                pos = i;
                break;
            }
        }
        if (pos >= 0){
            mPackageNameSpinner.setSelection(pos,false);
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }


    private class ApplicationPackagesAdapter extends BaseAdapter {

        private int itemViewResourceId = R.layout.application_package_item;
        private List<ApplicationPackage> items;

        public ApplicationPackagesAdapter(@NonNull Context context, @NonNull List<ApplicationPackage> applicationPackages) {
            this.items = applicationPackages;
        }

        private void setList(List<ApplicationPackage> applicationPackages) {
            this.items = applicationPackages;
        }

        public void replaceData(List<ApplicationPackage> applicationPackages) {
            setList(applicationPackages);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return createView(position, convertView, parent);
        }

        public class ViewHolder{
            public ImageView logo;
            public TextView packageName;
        }


        private View createView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            View rowView = convertView;

            if (rowView == null){
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(itemViewResourceId, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.logo = rowView.findViewById(R.id.application_package_item_logo);
                viewHolder.packageName = rowView.findViewById(R.id.application_package_item_name);
                rowView.setTag(viewHolder);
            }

            ViewHolder viewHolder = (ViewHolder) rowView.getTag();

            final ApplicationPackage applicationPackage = (ApplicationPackage) getItem(position);
//            Log.e("AppPackageName", applicationPackage.getName());
//            Log.e("AppPackage", applicationPackage.getPackageName());

//            Log.e("packageName", applicationPackage.getName() + ": " + applicationPackage.getPackageName());

            if (applicationPackage != null){
                if (applicationPackage.getPackageName().equals(ApplicationPackageLocalDataSource.ALL_APPLICATION_PACKAGES_STRING)) {
                    viewHolder.packageName.setText(getResources().getString(R.string.all_applications));
                    viewHolder.packageName.setTextSize(25);
                    viewHolder.logo.setVisibility(View.GONE);
                }
                else {
                    viewHolder.packageName.setText(applicationPackage.getName());
                    viewHolder.packageName.setTextSize(15);
                    viewHolder.logo.setVisibility(View.VISIBLE);
                    PackageManager packageManager = getContext().getPackageManager();
                    try {
                        viewHolder.logo.setImageDrawable(packageManager.getApplicationIcon(applicationPackage.getPackageName()));
                    } catch (PackageManager.NameNotFoundException e) {
                        viewHolder.logo.setImageResource(android.R.drawable.sym_def_app_icon);
                    }
                }

            }

            return rowView;
        }
    }

}
