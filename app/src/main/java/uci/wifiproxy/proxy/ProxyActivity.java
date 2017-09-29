package uci.wifiproxy.proxy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import uci.wifiproxy.R;
import uci.wifiproxy.profile.profilesList.ProfilesListActivity;
import uci.wifiproxy.proxy.service.ProxyService;
import uci.wifiproxy.util.ActivityUtils;

public class ProxyActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SHARED_PREFERENCES_NAME = "WifiProxy.conf";

    public static int LIGHT_THEME = 0;
    public static int DARK_THEME = 1;
    public int themeId;

    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        chargeTheme();
        setContentView(R.layout.proxy_act);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(getApplicationContext().getResources().getDrawable(R.mipmap.ic_launcher));
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.getMenu().findItem(R.id.proxy_navigation_menu_item).setChecked(true);
        }

        ProxyFragment proxyFragment = (ProxyFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (proxyFragment == null){
            //create the fragment
            proxyFragment = ProxyFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), proxyFragment, R.id.contentFrame);
        }

        //create the presenter
        new ProxyPresenter(proxyFragment, getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE));

        //Load previously saved state, if available
        if (savedInstanceState != null) {
            //TODO
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //used to configure the form when it is restarted
        //if closed by the system
//        if (isProxyServiceRunning(this)) {
//            disableAll();
//        } else {
//            enableAll();
//        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.proxy_navigation_menu_item:
                item.setChecked(true);
                break;
            case R.id.profile_navigation_menu_item:
                Intent intent = new Intent(ProxyActivity.this, ProfilesListActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        // Close the navigation drawer when an item is selected.
        mDrawerLayout.closeDrawers();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //no menu needed at this time
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_about_us:
                AlertDialog alertDialog = new AlertDialog.Builder(ProxyActivity.this).create();
                if (themeId == ProxyActivity.DARK_THEME) {
                    alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(ProxyActivity.this, R.style.AlertDialogCustom)).create();
                }
                alertDialog.setTitle(getResources().getString(R.string.createdBy));
                alertDialog.setMessage("Daniel A. Rodriguez Caballero: \n" +
                        "darodriguez@estudiantes.uci.cu,\n" + "danielrodcaball@gmail.com");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog.show();
                return true;

            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void chargeTheme() {
//        SharedPreferences settings = getSharedPreferences("UCIntlm.conf",
//                Context.MODE_PRIVATE);
//        themeId = settings.getInt("theme", LIGHT_THEME);
//        if (themeId == LIGHT_THEME) {
//            setTheme(R.style.AppTheme_NoActionBar);
//        } else if (themeId == DARK_THEME) {
//            setTheme(R.style.DarkTheme_NoActionBar);
//        }
//    }
//
    private int fetchPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }
//
//    private int fetchAccentColor() {
//        TypedValue typedValue = new TypedValue();
//        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
//        int color = a.getColor(0, 0);
//        a.recycle();
//        return color;
//    }

    private void initUi() {

    }

//    private void loadConf() {
//        SharedPreferences settings = getSharedPreferences("WifiProxy.conf",
//                Context.MODE_PRIVATE);
//        userInfoTab.username.setText(settings.getString("username", ""));
//        userInfoTab.pass.setText(Encripter.decrypt(settings.getString("password", "")));
//
//        preferencesTab.domain.setText(settings.getString("domain", ""));
//        preferencesTab.server.setText(settings.getString("server", ""));
//        preferencesTab.inputport.setText(settings.getString("inputport", ""));
//        preferencesTab.outputport.setText(settings.getString("outputport", ""));
//        preferencesTab.bypass.setText(settings.getString("bypass", ""));
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            (preferencesTab.globalCheckBox).setChecked(settings.getBoolean("global_proxy", true));
//        }
//
//        preferencesTab.spinnerTheme.setSelection(themeId);
//
//        if (userInfoTab.username.getText().toString().equals("")) {
//            userInfoTab.username.requestFocus();
//        } else {
//            userInfoTab.pass.requestFocus();
//        }
//
//        preferencesTab.authSchemeSpinner.setSelection(settings.getInt("authSchemeSelectedPos", 0));
//    }
//
//    private void saveConf() {
//        SharedPreferences settings = getSharedPreferences("WifiProxy.conf",
//                Context.MODE_PRIVATE);
//        Editor editor = settings.edit();
//        editor.putString("username", userInfoTab.username.getText().toString());
//        editor.putString("password",
//                Encripter.encrypt(userInfoTab.pass.getText().toString()));
//
//        editor.putString("domain", preferencesTab.domain.getText().toString());
//        editor.putString("server", preferencesTab.server.getText().toString());
//        editor.putString("inputport", preferencesTab.inputport.getText().toString());
//        editor.putString("outputport", preferencesTab.outputport.getText().toString());
//        editor.putString("bypass", preferencesTab.bypass.getText().toString());
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            editor.putBoolean("global_proxy", (preferencesTab.globalCheckBox).isChecked());
//        }
//
//        editor.putInt("authSchemeSelectedPos", preferencesTab.authSchemeSpinner.getSelectedItemPosition());
//        editor.apply();
//    }

    public void clickRun(View arg0) {
//        viewPager.setCurrentItem(0, true);
//
//        if ((preferencesTab.authSchemeSpinner.getSelectedItemPosition() == 1 && preferencesTab.domain.getText().toString().equals(""))
//                || preferencesTab.server.getText().toString().equals("")
//                || preferencesTab.inputport.getText().toString().equals("")
//                || preferencesTab.outputport.getText().toString().equals("")
//                || userInfoTab.username.getText().toString().equals("")
//                || userInfoTab.pass.getText().toString().equals("")) {
//
//            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.nodata),
//                    Toast.LENGTH_SHORT).show();
//            fab.setImageDrawable(new DrawableAwesome(R.string.fa_play, 35, Color.WHITE, false, false, 0, 0, 0, 0, this));
//            return;
//        }
//
//        if (!isProxyServiceRunning(this)) {
//            startProxy();
//        } else {
//            Intent proxyIntent = new Intent(this, ProxyService.class);
//            stopService(proxyIntent);
//
//            enableAll();
//
////            UCIntlmWidget.actualizarWidget(this.getApplicationContext(),
////                    AppWidgetManager.getInstance(this.getApplicationContext()),
////                    "off");
//        }
    }

    public void startProxy() {
//        Intent proxyIntent = new Intent(this, ProxyService.class);
//        saveConf();
//        proxyIntent.putExtra("user", userInfoTab.username.getText().toString());
//        proxyIntent.putExtra("pass", userInfoTab.pass.getText().toString());
//        proxyIntent.putExtra("domain", preferencesTab.domain.getText().toString());
//        proxyIntent.putExtra("server", preferencesTab.server.getText().toString());
//        proxyIntent.putExtra("inputport", preferencesTab.inputport.getText().toString());
//        proxyIntent.putExtra("outputport", preferencesTab.outputport.getText().toString());
//        proxyIntent.putExtra("bypass", preferencesTab.bypass.getText().toString());
//        switch (preferencesTab.authSchemeSpinner.getSelectedItemPosition()){
//            case 0:
//                proxyIntent.putExtra("authScheme", HttpForwarder1.BASIC_SCHEME);
//                break;
//            case 1:
//                proxyIntent.putExtra("authScheme", HttpForwarder1.NTLM_SCHEME);
//                break;
//            case 2:
//                proxyIntent.putExtra("authScheme", HttpForwarder1.DIGEST_SCHEME);
//                break;
//        }
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            proxyIntent.putExtra("set_global_proxy", preferencesTab.globalCheckBox.isChecked());
//        } else {
//            proxyIntent.putExtra("set_global_proxy", false);
//        }
//
//        startService(proxyIntent);
//        disableAll();
    }

    private static boolean isProxyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (ProxyService.class.getName().equals(
                    service.service.getClassName())) {
                Log.i(ProxyActivity.class.getName(), "Service running");
                return true;
            }
        }
        Log.i(ProxyActivity.class.getName(), "Service not running");
        return false;
    }

}

