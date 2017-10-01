package uci.wifiproxy.firewall.firewallRulesList;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import uci.wifiproxy.R;
import uci.wifiproxy.profile.profilesList.ProfilesListActivity;
import uci.wifiproxy.proxy.ProxyActivity;
import uci.wifiproxy.util.ActivityUtils;

/**
 * Created by daniel on 29/09/17.
 */

public class FirewallRulesListActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firewall_rules_list_act);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.firewall_title);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        //Set up navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
            navigationView.getMenu().findItem(R.id.firewall_navigation_menu_item).setChecked(true);
        }

        FirewallRulesListFragment firewallRulesListFragment =
                (FirewallRulesListFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (firewallRulesListFragment == null){
            firewallRulesListFragment = FirewallRulesListFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    firewallRulesListFragment, R.id.contentFrame);
        }

        //create the presenter
        new FirewallRulesListPresenter(firewallRulesListFragment);

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

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Intent intent = null;
                        switch (menuItem.getItemId()) {
                            case R.id.proxy_navigation_menu_item:
                                intent = new Intent(FirewallRulesListActivity.this, ProxyActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                break;
                            case R.id.profile_navigation_menu_item:
                                intent = new Intent(FirewallRulesListActivity.this, ProfilesListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                break;
                            case R.id.firewall_navigation_menu_item:
                                menuItem.setChecked(true);
                                break;
                            default:
                                break;
                        }

                        // Close the navigation drawer when an item is selected.
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
}
