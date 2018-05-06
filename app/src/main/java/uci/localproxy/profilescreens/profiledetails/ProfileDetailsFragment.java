package uci.localproxy.profilescreens.profiledetails;

import android.content.Intent;
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
import android.widget.TextView;

import uci.localproxy.R;
import uci.localproxy.profilescreens.addeditprofile.AddEditProfileActivity;
import uci.localproxy.profilescreens.addeditprofile.AddEditProfileFragment;

/**
 * Created by daniel on 18/09/17.
 */

public class ProfileDetailsFragment extends Fragment implements ProfileDetailsContract.View {

    @NonNull
    public static final String ARGUMENT_PROFILE_ID = "PROFILE_ID";

    @NonNull
    public static final int REQUEST_EDIT_PROFILE = 1;

    private ProfileDetailsContract.Presenter mPresenter;

    private TextView mDetailName;

    private TextView mDetailServer;

    private TextView mDetailInPort;

    private TextView mDetailBypass;

//    private TextView mDomain;

    public ProfileDetailsFragment(){
        //Requires an empty public constructor
    }

    public static ProfileDetailsFragment newInstance(){
        return new ProfileDetailsFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.profile_details_frag, container, false);
        mDetailName = root.findViewById(R.id.nameTv);
        mDetailServer = root.findViewById(R.id.serverTv);
        mDetailInPort = root.findViewById(R.id.inPortTv);
        mDetailBypass = root.findViewById(R.id.bypassTv);
//        mDomain = (TextView) root.findViewById(R.id.domainTv);

        FloatingActionButton fab = getActivity().findViewById(R.id.fab_edit_profile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.editProfile();
            }
        });

        setHasOptionsMenu(true);

        return root;

    }

    @Override
    public void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profiledetail_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_delete:
                mPresenter.deleteProfile();
                break;
        }
        return false;
    }

    @Override
    public void showMissingProfile() {
        Snackbar.make(mDetailName, R.string.missing_data_message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showName(String name) {
        mDetailName.setText(name);
    }

    @Override
    public void showServer(String server) {
        mDetailServer.setText(server);
    }

    @Override
    public void showInPort(int inPort) {
        mDetailInPort.setText(String.valueOf(inPort));
    }

    @Override
    public void showBypass(String bypass) {
        mDetailBypass.setText(bypass);
    }

//    @Override
//    public void showDomain(String domain) {
//        mDomain.setText(domain);
//    }

    @Override
    public void showEditProfile(String profileId) {
        Intent i = new Intent(getActivity(), AddEditProfileActivity.class);
        i.putExtra(AddEditProfileFragment.ARGUMENT_EDIT_PROFILE_ID, profileId);
        startActivityForResult(i, REQUEST_EDIT_PROFILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode);
    }

    @Override
    public void showProfileDeleted() {
        getActivity().finish();
    }

    @Override
    public void showSuccessfullyUpdatedMessage() {
        Snackbar.make(mDetailName,
                getResources().getString(R.string.successfully_profile_updated_message),
                Snackbar.LENGTH_LONG).show();
    }


    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(ProfileDetailsContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
