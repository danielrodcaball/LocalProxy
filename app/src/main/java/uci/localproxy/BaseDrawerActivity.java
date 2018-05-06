package uci.localproxy;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import uci.localproxy.aboutscreen.AboutActivity;
import uci.localproxy.firewallscreens.firewallruleslist.FirewallRulesListActivity;
import uci.localproxy.profilescreens.profileslist.ProfilesListActivity;
import uci.localproxy.proxyscreen.ProxyActivity;
import uci.localproxy.tracescreens.traceslist.TracesListActivity;

/**
 * Created by daniel on 17/02/18.
 */

public abstract class BaseDrawerActivity extends AppCompatActivity {

    protected DrawerLayout mDrawerLayout;

    protected void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Intent intent = null;
                        switch (menuItem.getItemId()) {
                            case R.id.proxy_navigation_menu_item:
                                if (BaseDrawerActivity.this instanceof ProxyActivity) {
                                    menuItem.setChecked(true);
                                } else {
                                    intent = new Intent(BaseDrawerActivity.this, ProxyActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                }
                                break;
                            case R.id.profile_navigation_menu_item:
                                if (BaseDrawerActivity.this instanceof ProfilesListActivity) {
                                    menuItem.setChecked(true);
                                } else {
                                    intent = new Intent(BaseDrawerActivity.this, ProfilesListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                }
                                break;
                            case R.id.firewall_navigation_menu_item:
                                if (BaseDrawerActivity.this instanceof FirewallRulesListActivity) {
                                    menuItem.setChecked(true);
                                } else {
                                    intent = new Intent(BaseDrawerActivity.this, FirewallRulesListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                }
                                break;
                            case R.id.traces_navigation_menu_item:
                                if (BaseDrawerActivity.this instanceof TracesListActivity) {
                                    menuItem.setChecked(true);
                                } else {
                                    intent = new Intent(BaseDrawerActivity.this, TracesListActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                }
                                break;
                            case R.id.about_menu_item:
                                intent = new Intent(BaseDrawerActivity.this, AboutActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);

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
