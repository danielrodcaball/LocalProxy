package uci.wifiproxy.profile.profilesList;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uci.wifiproxy.R;
import uci.wifiproxy.data.profile.Profile;
import uci.wifiproxy.profile.addEditProfile.AddEditProfileActivity;
import uci.wifiproxy.profile.profileDetails.ProfileDetailsActivity;

/**
 * Created by daniel on 16/09/17.
 */

public class ProfilesListFragment extends Fragment implements ProfilesListContract.View {

    private ProfilesListContract.Presenter mPresenter;

    private ProfilesAdapter mListAdapter;

    private View mNoProfilesView;

    private LinearLayout mProfilesView;

    ProfileItemListener mItemListener = new ProfileItemListener() {
        @Override
        public void onProfileClick(Profile clickedProfile) {
            mPresenter.openProfileDetails(clickedProfile);
        }
    };

    public ProfilesListFragment(){
        // Requires empty public constructor
    }

    public static ProfilesListFragment newInstance() {
        return new ProfilesListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new ProfilesAdapter(new ArrayList<Profile>(0), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.profiles_list_frag, container, false);

        //Set up profiles view
        ListView listView = (ListView) root.findViewById(R.id.profiles_list);
        listView.setAdapter(mListAdapter);
        mProfilesView = (LinearLayout) root.findViewById(R.id.profilesLL);

        //Set up no profiles view
        mNoProfilesView = root.findViewById(R.id.noProfiles);
        TextView mNoProfilesAddView = (TextView) root.findViewById(R.id.noProfilesAdd);
        mNoProfilesAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProfile();
            }
        });

        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_task);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewProfile();
            }
        });

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void setPresenter(ProfilesListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        //TODO
    }

    @Override
    public void showProfiles(List<Profile> profiles) {
        mListAdapter.replaceData(profiles);

        mProfilesView.setVisibility(View.VISIBLE);
        mNoProfilesView.setVisibility(View.GONE);
    }

    @Override
    public void showNoProfiles() {
        mProfilesView.setVisibility(View.GONE);
        mNoProfilesView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAddProfile() {
        Intent intent = new Intent(getContext(), AddEditProfileActivity.class);
        startActivityForResult(intent, AddEditProfileActivity.REQUEST_ADD_PROFILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode);
    }

    @Override
    public void showProfileDetailsUI(String profileId) {
        Intent i = new Intent(getContext(), ProfileDetailsActivity.class);
        i.putExtra(ProfileDetailsActivity.EXTRA_PROFILE_ID, profileId);
        startActivity(i);
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getResources().getString(R.string.successfully_saved_profile_message));
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    private void showMessage(String message){
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    private static class ProfilesAdapter extends BaseAdapter {

        private List<Profile> mProfiles;
        private ProfileItemListener mItemListener;

        public ProfilesAdapter(List<Profile> profiles, ProfileItemListener itemListener){
            setList(profiles);
            mItemListener = itemListener;
        }

        private void setList(List<Profile> profiles){
            mProfiles = profiles;
        }

        public void replaceData(List<Profile> profiles){
            setList(profiles);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mProfiles.size();
        }

        @Override
        public Profile getItem(int position) {
            return mProfiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder{
            public TextView titleTV;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null){
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.profiles_list_item, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.titleTV = (TextView) rowView.findViewById(R.id.title);
                rowView.setTag(viewHolder);
            }

            ViewHolder viewHolder = (ViewHolder) rowView.getTag();

            final Profile profile = getItem(position);

            viewHolder.titleTV.setText(profile.getName());

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemListener.onProfileClick(profile);
                }
            });

            return rowView;
        }
    }

    public interface ProfileItemListener {
        void onProfileClick(Profile clickedProfile);
    }

}
