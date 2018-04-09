package uci.wifiproxy.profilescreens.addeditprofile;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import uci.wifiproxy.R;

/**
 * Created by daniel on 18/09/17.
 */

public class AddEditProfileFragment extends Fragment implements AddEditProfileContract.View {

    public static final String ARGUMENT_EDIT_PROFILE_ID = "EDIT_PROFILE_ID";

    private AddEditProfileContract.Presenter mPresenter;

    private TextView mName;

    private TextView mServer;

    private TextView mInPort;

    private TextView mBypass;

    private TextView mDomain;

    public AddEditProfileFragment() {
        // Required empty public constructor
    }

    public static AddEditProfileFragment newInstance() {
        return new AddEditProfileFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_profile_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.saveProfile(
                        mName.getText().toString(),
                        mServer.getText().toString(),
                        mInPort.getText().toString(),
                        mBypass.getText().toString(),
                        mDomain.getText().toString()
                );
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.addprofile_frag, container, false);
        mName = (TextView) root.findViewById(R.id.ename);
        mServer = (TextView) root.findViewById(R.id.eserver);
        mInPort = (TextView) root.findViewById(R.id.einputport);
        mBypass = (TextView) root.findViewById(R.id.ebypass);
        mBypass.setText(getString(R.string.bypassInitText));
        mDomain = (TextView) root.findViewById(R.id.domain);

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void setPresenter(@NonNull AddEditProfileContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showEmptyProfileError() {
        Snackbar.make(mName, getString(R.string.empty_profile_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void finishAddEditProfileActivity() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void setName(String name) {
        mName.setText(name);
    }

    @Override
    public void setServer(String server) {
        mServer.setText(server);
    }

    @Override
    public void setInPort(String inPort) {
        mInPort.setText(inPort);
    }

    @Override
    public void setBypass(String bypass) {
        mBypass.setText(bypass);
    }

    @Override
    public void setDomain(String domain) {
        mDomain.setText(domain);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setProfileEqualNameError() {
        mName.setError("There is a profile with that name");
    }

    @Override
    public void setNameEmptyError() {
        mName.setError("Name cannot be empty");
    }

    @Override
    public void setServerEmptyError() {
        mServer.setError("Server cannot be empty");
    }

    @Override
    public void setInPortEmptyError() {
        mInPort.setError("Input port cannot be empty");
    }

    @Override
    public void setInputPortOutOfRangeError() {
        mInPort.setError(
                String.format(Locale.ENGLISH, "Input port must be between %d and %d",
                        0, AddEditProfilePresenter.MAX_PORTS_LIMIT)
        );
    }

    @Override
    public void setBypassSyntaxError() {
        mBypass.setError("Bypass syntax error");
    }

}