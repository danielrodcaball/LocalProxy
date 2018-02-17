package uci.wifiproxy.trace.tracesList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import uci.wifiproxy.BaseDrawerActivity;
import uci.wifiproxy.R;
import uci.wifiproxy.util.ActivityUtils;

/**
 * Created by daniel on 16/02/18.
 */

public class TracesListActivity extends BaseDrawerActivity {

//    private DrawerLayout mDrawer;
    private TracesListContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traces_list_act);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.traces_title));
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
            navigationView.getMenu().findItem(R.id.traces_navigation_menu_item).setChecked(true);
        }

        TracesListFragment tracesListFragment =
                (TracesListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tracesListFragment == null) {
            tracesListFragment = TracesListFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), tracesListFragment, R.id.contentFrame);
        }

        //Create the presenter
        mPresenter = new TracesListPresenter(tracesListFragment);

        //Load previously saved state, if available
        if (savedInstanceState != null) {
            //TODO
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void setupDrawerContent(NavigationView navigationView) {
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        Intent intent = null;
//                        switch (menuItem.getItemId()) {
//                            case R.id.proxy_navigation_menu_item:
//                                intent = new Intent(TracesListActivity.this, ProxyActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                startActivity(intent);
//                                break;
//                            case R.id.profile_navigation_menu_item:
//                                intent = new Intent(TracesListActivity.this, ProfilesListActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                startActivity(intent);
//                                break;
//                            case R.id.firewall_navigation_menu_item:
//                                intent = new Intent(TracesListActivity.this, FirewallRulesListActivity.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                startActivity(intent);
//                                break;
//                            case R.id.traces_navigation_menu_item:
//                                menuItem.setChecked(true);
//                                break;
//                            default:
//                                break;
//                        }
//                        // Close the navigation drawer when an item is selected.
//                        mDrawer.closeDrawers();
//                        return true;
//                    }
//                });
//    }
}



