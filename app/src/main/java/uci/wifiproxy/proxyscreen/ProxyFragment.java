package uci.wifiproxy.proxyscreen;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uci.wifiproxy.R;
import uci.wifiproxy.WifiProxyApplication;
import uci.wifiproxy.data.profile.Profile;
import uci.wifiproxy.data.user.User;
import uci.wifiproxy.profilescreens.addeditprofile.AddEditProfileActivity;
import uci.wifiproxy.profilescreens.addeditprofile.AddEditProfilePresenter;
import uci.wifiproxy.proxycore.ProxyService;

import com.github.chrisbanes.photoview.PhotoViewAttacher;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by daniel on 15/09/17.
 */

public class ProxyFragment extends Fragment implements ProxyContract.View {

    private ProxyContract.Presenter mPresenter;

    private AutoCompleteTextView mUsername;

    private TextView mPassword;

    private CheckBox mRememberPasswordCheck;

    private Spinner mProfileSpinner;

    private Button mAddProfileButton;

    @Nullable
    private CheckBox mGlobalProxyCheck;

    private EditText mLocalPortEditText;

    private FloatingActionButton mFabStartProxy;

    private FloatingActionButton mFabStopProxy;

    private ArrayAdapter<Profile> mProfileArrayAdapter;

    private UsersArrayAdapter mUserArrayAdapter;

    private ProgressDialog mProgressDialog;

//    private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int message = intent.getIntExtra(ProxyService.MESSAGE_TAG, ProxyService.SERVICE_STARTED_SUCCESSFUL);
//            Log.e("serviceMessage", message+"");
//            mPresenter.startServiceResult(message);
//        }
//    };


    public static ProxyFragment newInstance() {
        return new ProxyFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProfileArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        mProfileArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUserArrayAdapter = new UsersArrayAdapter(getContext(), new ArrayList<User>(0));
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
//        LocalBroadcastManager.getInstance(getContext()).registerReceiver(
//                serviceReceiver, new IntentFilter(ProxyService.SERVICE_RECIVER_NAME));
//        mUsername.requestFocus();
    }

    @Override
    public void onPause() {
//        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(
//                serviceReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFabStartProxy = getActivity().findViewById(R.id.fab_start_proxy);
        mFabStartProxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profileId = (mProfileSpinner.getSelectedItem() == null) ? ""
                        : ((Profile) mProfileSpinner.getSelectedItem()).getId();

                mPresenter.startProxyFromFabButton(mUsername.getText().toString(),
                        mPassword.getText().toString(),
                        profileId,
                        mLocalPortEditText.getText().toString(),
                        mRememberPasswordCheck.isChecked(),
                        mGlobalProxyCheck.isChecked());
            }
        });

        mFabStopProxy = getActivity().findViewById(R.id.fab_stop_proxy);
        mFabStopProxy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.stopProxy();
            }
        });

        mProgressDialog = new ProgressDialog(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.proxy_frag, container, false);

        mUsername = root.findViewById(R.id.euser);
        mUsername.setAdapter(mUserArrayAdapter);

        mPassword = root.findViewById(R.id.epass);
        mRememberPasswordCheck = root.findViewById(R.id.check_rem_pass);

        mLocalPortEditText = root.findViewById(R.id.local_port);

        mGlobalProxyCheck = root.findViewById(R.id.globCheckBox);
        if (Build.VERSION.SDK_INT > WifiProxyApplication.MAX_SDK_SUPPORTED_FOR_WIFI_CONF) {
            mGlobalProxyCheck.setVisibility(View.GONE);
        }

        mProfileSpinner = root.findViewById(R.id.spinner_profiles);
        mProfileSpinner.setAdapter(mProfileArrayAdapter);

        mAddProfileButton = root.findViewById(R.id.add_profile_button);
        mAddProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewProfile();
            }
        });

//        ButtonAwesome buttonViewPass = (ButtonAwesome) root.findViewById(R.id.buttonViewPass);
//        buttonViewPass.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                }
//                return false;
//            }
//        });

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void setPresenter(@NonNull ProxyContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void enableAllViews() {
        mUsername.setEnabled(true);
        mPassword.setEnabled(true);
        mRememberPasswordCheck.setEnabled(true);
        mProfileSpinner.setEnabled(true);
        mLocalPortEditText.setEnabled(true);

        if (mGlobalProxyCheck != null) {
            mGlobalProxyCheck.setEnabled(true);
        }
    }

    @Override
    public void disableAllViews() {
        mUsername.setEnabled(false);
        mPassword.setEnabled(false);
        mRememberPasswordCheck.setEnabled(false);
        mProfileSpinner.setEnabled(false);
        mLocalPortEditText.setEnabled(false);

        if (mGlobalProxyCheck != null) {
            mGlobalProxyCheck.setEnabled(false);
        }
    }

    @Override
    public void setPlayView() {
        mFabStartProxy.setVisibility(View.VISIBLE);
        mFabStopProxy.setVisibility(View.GONE);
    }

    @Override
    public void setStopView() {
        mFabStopProxy.setVisibility(View.VISIBLE);
        mFabStartProxy.setVisibility(View.GONE);
    }

    @Override
    public void setUsername(String username) {
        mUsername.setText(username);
    }

    @Override
    public void setPassword(String password) {
        mPassword.setText(password);
    }

    @Override
    public void setRememberPassword(boolean remember) {
        mRememberPasswordCheck.setChecked(remember);
    }

    @Override
    public void setLocalPort(String localPort) {
        mLocalPortEditText.setText(localPort);
    }

    @Override
    public void setGlobalProxyChecked(boolean checked) {
        if (mGlobalProxyCheck != null) {
            mGlobalProxyCheck.setChecked(checked);
        }
    }

    @Override
    public void setSpinnerProfiles(List<Profile> profiles) {
        mProfileSpinner.setVisibility(View.VISIBLE);
        mAddProfileButton.setVisibility(View.GONE);

        mProfileArrayAdapter.clear();
        mProfileArrayAdapter.addAll(profiles);
        mProfileArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSpinnerProfileSelected(String profileId) {
        if (mProfileSpinner.getVisibility() == View.VISIBLE) {
            int pos = -1;
            for (int i = 0; i < mProfileArrayAdapter.getCount(); i++) {
                if (mProfileArrayAdapter.getItem(i).getId().equals(profileId)) {
                    pos = i;
                    break;
                }
            }

            if (pos >= 0) {
                mProfileSpinner.setSelection(pos, false);
            }
        }
    }

    @Override
    public void showNoProfilesView() {
        mProfileArrayAdapter.clear();
        mProfileArrayAdapter.notifyDataSetChanged();

        mProfileSpinner.setVisibility(View.GONE);
        mAddProfileButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void setUsernameEmptyError() {
        mUsername.setError(getString(R.string.username_empty_error));
    }

    @Override
    public void setPasswordEmptyError() {
        mPassword.setError(getString(R.string.password_empty_error));
    }

    @Override
    public void setProfileNoSelectedError() {
        Snackbar.make(mUsername, R.string.no_profile_selected_error, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setLocalPortEmptyError() {
        mLocalPortEditText.setError(getString(R.string.proxy_port_empty_error));
    }

    @Override
    public void setLocalPortOutOfRangeError() {
        mLocalPortEditText.setError(String.format(Locale.ENGLISH, getString(R.string.profile_port_outofrange_error),
                AddEditProfilePresenter.MAX_SYSTEM_PORTS_LIMIT,
                AddEditProfilePresenter.MAX_PORTS_LIMIT));
    }

    @Override
    public void setUsers(@NonNull List<User> users) {
        mUserArrayAdapter.replaceData(users);
    }

    @Override
    public void showAddProfile() {
        Intent intent = new Intent(getContext(), AddEditProfileActivity.class);
        startActivityForResult(intent, AddEditProfileActivity.REQUEST_ADD_PROFILE);
    }

    @Override
    public boolean isConnectedToAWifi() {
            ConnectivityManager connManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return mWifi.isConnected();
    }


//    @Override
//    public boolean isProxyServiceRunning() {
//        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager
//                .getRunningServices(Integer.MAX_VALUE)) {
//            if (ProxyService.class.getName().equals(
//                    service.service.getClassName())) {
//                Log.i(ProxyActivity.class.getName(), "Service running");
//                return true;
//            }
//        }
//        Log.i(ProxyActivity.class.getName(), "Service not running");
//        return false;
//    }

    @Override
    public void startProxyService(String username,
                                  String password,
                                  String server,
                                  int inputport,
                                  int outputport,
                                  String bypass,
                                  String domain,
                                  boolean setGlobProxy
    ) {
        Intent proxyIntent = new Intent(getActivity(), ProxyService.class);
        proxyIntent.putExtra("user", username);
        proxyIntent.putExtra("pass", password);
        proxyIntent.putExtra("server", server);
        proxyIntent.putExtra("inputport", inputport + "");
        proxyIntent.putExtra("outputport", outputport + "");
        proxyIntent.putExtra("bypass", bypass);
        proxyIntent.putExtra("domain", domain);
        proxyIntent.putExtra("set_global_proxy", setGlobProxy);
        getActivity().startService(proxyIntent);
    }

    @Override
    public void showWifiConfDialog() {
        createWifiAlertDialog().show();
    }

    @Override
    public void startWifiConfActivity() {
        Intent i = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getContext().startActivity(i);
    }

    @Override
    public void showProgressDialog(boolean show) {
        if (show) {
            mProgressDialog.setMessage(getString(R.string.proxy_credentials_check_message));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
        else {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void showWrongCredentialsDialog() {
        createAlertDialog(getString(R.string.proxy_credentials_error),
                getString(R.string.proxy_credentials_error_message)).show();
    }

    @Override
    public void showNetworkError() {
        createAlertDialog(getString(R.string.proxy_network_error),
                getString(R.string.proxy_network_error_message)).show();
    }

    @Override
    public void showConnectionTimeOutError() {
        createAlertDialog(getString(R.string.proxy_connectiontimeout_error),
                getString(R.string.proxy_connectiontimeout_error_message)).show();
    }

    @Override
    public void showUnknownHostError() {
        createAlertDialog(getString(R.string.proxy_unknownhost_error),
                getString(R.string.proxy_unknownhost_error_message)).show();
    }

    @Override
    public void showConnectionError() {
        createAlertDialog(getString(R.string.proxy_connection_error),
                getString(R.string.proxy_connection_error_message)).show();
    }

    @Override
    public void showErrorStartingService() {
        createAlertDialog(getString(R.string.error_starting_proxy_service_title),
                getString(R.string.local_port_not_available)).show();
    }

    @Override
    public void setLocalPortNotAvailable() {
        createAlertDialog(getString(R.string.port_not_available_title),
                getString(R.string.local_port_not_available)).show();
//        mLocalPortEditText.setError(getString(R.string.port_not_available_title));
    }

    private AlertDialog createAlertDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.accept_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();
    }

    @Override
    public void stopProxyService() {
        Intent proxyIntent = new Intent(getActivity(), ProxyService.class);
        getActivity().stopService(proxyIntent);
    }

    private AlertDialog createWifiAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = ((AppCompatActivity) getContext()).getLayoutInflater();
        View view = inflater.inflate(R.layout.wifi_alert_dialog, null);
        ImageView wifiConfigImage = view.findViewById(R.id.wifiConfigImageView);
        PhotoViewAttacher mAtacher = new PhotoViewAttacher(wifiConfigImage);
        final CheckBox dontShowCheckBox = view.findViewById(R.id.dontShowCheckBox);
        builder.setTitle(getContext().getResources().getString(R.string.wifiSettings));
        builder.setView(view);
        builder.setPositiveButton(R.string.wifiSettingsGoToWifi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.goToWifiSettings(dontShowCheckBox.isChecked());
            }
        });
        builder.setNegativeButton(R.string.wifiSettingsContinue, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String profileId = (mProfileSpinner.getSelectedItem() == null) ? ""
                        : ((Profile) mProfileSpinner.getSelectedItem()).getId();
                mPresenter.startProxyFromHelpDialog(mUsername.getText().toString(),
                        mPassword.getText().toString(),
                        profileId,
                        mLocalPortEditText.getText().toString(),
                        mRememberPasswordCheck.isChecked(),
                        mGlobalProxyCheck.isChecked(),
                        dontShowCheckBox.isChecked());
                dialog.cancel();
            }
        });

        return builder.create();
    }


    private class UsersArrayAdapter extends ArrayAdapter<User> {

        private int viewResourceId = R.layout.user_list_item;

        public UsersArrayAdapter(@NonNull Context context, @NonNull ArrayList<User> users) {
            super(context, R.layout.user_list_item, users);
        }

        private void setList(List<User> users) {
            clear();
            addAll(users);
        }

        public void replaceData(List<User> users) {
            setList(users);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(viewResourceId, parent, false);
            }

            final User user = getItem(position);

            TextView username = rowView.findViewById(R.id.user_username);

            if (user != null) {
                username.setText(user.getUsername());
            }

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUsername.setText(user.getUsername());
                    String password = user.getPassword();
                    mPassword.setText(password);

                    if (Strings.isNullOrEmpty(password))
                        mRememberPasswordCheck.setChecked(false);
                    else
                        mRememberPasswordCheck.setChecked(true);

                    mUsername.dismissDropDown();
                }
            });

            return rowView;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return usernameFilter;
        }

        Filter usernameFilter = new Filter() {

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                String str = ((User) resultValue).getUsername();
                return str;
            }

            @Override
            protected FilterResults performFiltering(final CharSequence constraint) {
                // This is performed in a worker thread, I have nothing to do here because my realm
                //instance is in the UI thread, this is a kind of issue but it's a few data that not affect
                // the view. I'm going to handle the filtering in the publishResults method.
                // I have to do this because Realm doesn't have any good implementation for this, for now
                // THIS WORK!!!!!!.
                final FilterResults results = new FilterResults();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //This is executed in the UI thread, PERFECT!!!!
                if (constraint != null && !constraint.equals("")) {
                    mPresenter.filterUsers(constraint.toString());
                } else {
                    mPresenter.loadUsers();
                }
            }
        };
    }

}
