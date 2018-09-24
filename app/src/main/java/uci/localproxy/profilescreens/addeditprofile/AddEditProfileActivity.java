package uci.localproxy.profilescreens.addeditprofile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import uci.localproxy.R;
import uci.localproxy.util.ActivityUtils;

/**
 * Created by daniel on 18/09/17.
 */

public class AddEditProfileActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_PROFILE = 1;

    public static final String SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY";

    private AddEditProfileContract.Presenter mPresenter;

    private ActionBar mActionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addprofile_act);

        // Set up the toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        AddEditProfileFragment addEditProfileFragment = (AddEditProfileFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        String profileId = getIntent().getStringExtra(AddEditProfileFragment.ARGUMENT_EDIT_PROFILE_ID);

        setToolbarTitle(profileId);

        if (addEditProfileFragment == null){
            addEditProfileFragment = AddEditProfileFragment.newInstance();

            if (getIntent().hasExtra(AddEditProfileFragment.ARGUMENT_EDIT_PROFILE_ID)){
                Bundle bundle = new Bundle();
                bundle.putString(AddEditProfileFragment.ARGUMENT_EDIT_PROFILE_ID, profileId);
                addEditProfileFragment.setArguments(bundle);
            }

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditProfileFragment, R.id.contentFrame);
        }

        boolean shouldLoadDataFromRepo = true;

        // Prevent the presenter from loading data from the repository if this is a config change.
        if (savedInstanceState != null) {
            // Data might not have loaded when the config change happen, so we saved the state.
            shouldLoadDataFromRepo = savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY);
        }

        // Create the presenter
        mPresenter = new AddEditProfilePresenter(
                addEditProfileFragment,
                profileId,
                shouldLoadDataFromRepo
        );
    }

    private void setToolbarTitle(@Nullable String profileId) {
        if(profileId == null) {
            mActionBar.setTitle(R.string.add_profile);
        } else {
            mActionBar.setTitle(R.string.edit_profile);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, mPresenter.isDataMissing());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
