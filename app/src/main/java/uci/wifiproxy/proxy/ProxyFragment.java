package uci.wifiproxy.proxy;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.Strings;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import uci.wifiproxy.R;
import uci.wifiproxy.data.profile.Profile;
import uci.wifiproxy.data.user.User;
import uci.wifiproxy.profile.addEditProfile.AddEditProfileActivity;
import uci.wifiproxy.util.fontAwesome.ButtonAwesome;

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

    private FloatingActionButton fab;

    private ArrayAdapter<Profile> mProfileArrayAdapter;

    private UsersArrayAdapter mUserArrayAdapter;


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
//        mUsername.requestFocus();
    }

    @Override
    public void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profileId = (mProfileSpinner.getSelectedItem() == null) ? ""
                        : ((Profile) mProfileSpinner.getSelectedItem()).getId();

                if (mGlobalProxyCheck != null) {
                    mPresenter.startProxy(mUsername.getText().toString(),
                            mPassword.getText().toString(),
                            profileId,
                            mRememberPasswordCheck.isChecked(),
                            mGlobalProxyCheck.isChecked());
                } else {
                    mPresenter.startProxy(mUsername.getText().toString(),
                            mPassword.getText().toString(),
                            profileId,
                            mRememberPasswordCheck.isChecked(),
                            false);
                }

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.proxy_frag, container, false);

        mUsername = (AutoCompleteTextView) root.findViewById(R.id.euser);
        mUsername.setAdapter(mUserArrayAdapter);
//        mUsername.setOnFocusChangeListener(new View.OnFocusChangeListener(){
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    mUsername.showDropDown();
//                }
//            }
//        });

        mPassword = (TextView) root.findViewById(R.id.epass);
        mRememberPasswordCheck = (CheckBox) root.findViewById(R.id.check_rem_pass);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mGlobalProxyCheck = (CheckBox) root.findViewById(R.id.globCheckBox);
        }

        mProfileSpinner = (Spinner) root.findViewById(R.id.spinner_profiles);
        mProfileSpinner.setAdapter(mProfileArrayAdapter);

        mAddProfileButton = (Button) root.findViewById(R.id.add_profile_button);
        mAddProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewProfile();
            }
        });

        ButtonAwesome buttonViewPass = (ButtonAwesome) root.findViewById(R.id.buttonViewPass);
        buttonViewPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPresenter.onTouchButtonViewPass(event.getAction());
                return false;
            }
        });

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void setPresenter(@NonNull ProxyContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void enableAllViews() {
//        //set the form to introduce data and start the service
//        userInfoTab.username.setEnabled(true);
//        userInfoTab.pass.setEnabled(true);
//        preferencesTab.domain.setEnabled(true);
//        preferencesTab.server.setEnabled(true);
//        preferencesTab.inputport.setEnabled(true);
//        preferencesTab.outputport.setEnabled(true);
//        preferencesTab.spinnerTheme.setEnabled(true);
//        preferencesTab.bypass.setEnabled(true);
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            preferencesTab.globalCheckBox.setEnabled(true);
//        } else {
//            preferencesTab.wiffiSettingsButton.setEnabled(true);
//        }
//
//        fab.setImageDrawable(new DrawableAwesome(R.string.fa_play, 35, Color.WHITE, false, false, 0, 0, 0, 0, getActivity()));
//
//        preferencesTab.authSchemeSpinner.setEnabled(true);
    }

    @Override
    public void disableAllViews() {
//        //set the form to disable all fields and change the button to stop the service
//        userInfoTab.username.setEnabled(false);
//        userInfoTab.pass.setEnabled(false);
//        preferencesTab.domain.setEnabled(false);
//        preferencesTab.server.setEnabled(false);
//        preferencesTab.inputport.setEnabled(false);
//        preferencesTab.outputport.setEnabled(false);
//        preferencesTab.spinnerTheme.setEnabled(false);
//        preferencesTab.bypass.setEnabled(false);
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            preferencesTab.globalCheckBox.setEnabled(false);
//        } else {
//            preferencesTab.wiffiSettingsButton.setEnabled(false);
//        }
//
//        fab.setImageDrawable(new DrawableAwesome(R.string.fa_stop, 35, Color.WHITE, false, false, 0, 0, 0, 0, getActivity()));
//        preferencesTab.authSchemeSpinner.setEnabled(false);
    }

    @Override
    public void setPlayView() {
        fab.setImageResource(R.drawable.ic_proxy_play);
    }

    @Override
    public void setStopView() {
        fab.setImageResource(R.drawable.ic_proxy_stop);
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
            int pos = 0;
            for (int i = 0; i < mProfileArrayAdapter.getCount(); i++) {
                if (mProfileArrayAdapter.getItem(i).getId().equals(profileId)) {
                    pos = i;
                    break;
                }
            }
            mProfileSpinner.setSelection(pos, false);
        }
    }

    @Override
    public void showNoProfilesView() {
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
    public void setUsers(@NonNull List<User> users) {
        mUserArrayAdapter.replaceData(users);
    }

    @Override
    public void setPasswordVisibility(boolean visibility) {
        if (visibility) {
            mPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    @Override
    public void showAddProfile() {
        Intent intent = new Intent(getContext(), AddEditProfileActivity.class);
        startActivityForResult(intent, AddEditProfileActivity.REQUEST_ADD_TASK);
    }

    private class UsersArrayAdapter extends ArrayAdapter<User> {

        private List<User> items;

        private int viewResourceId = R.layout.user_list_item;

        public UsersArrayAdapter(@NonNull Context context, @NonNull ArrayList<User> users) {
            super(context, R.layout.user_list_item, users);
            this.items = users;
        }

        private void setList(List<User> users) {
            items = users;
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

            TextView username = (TextView) rowView.findViewById(R.id.user_username);

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
                clear();
                if (constraint != null && items != null && items.size() > 0) {
                    addAll(((RealmResults<User>)items).where().beginsWith(User.USERNAME_FIELD,
                            constraint.toString().toLowerCase()).findAll());
                } else {
                    addAll(items);
                }
                notifyDataSetChanged();
            }
        };
    }

}
