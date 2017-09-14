package uci.wifiproxy.ui.ui;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;


import uci.wifiproxy.R;
import uci.wifiproxy.ntlm.core.HttpForwarder1;
import uci.wifiproxy.service.service.ProxyService;
import uci.wifiproxy.ui.Security.Encripter;
import uci.wifiproxy.ui.ui.fontAwesome.DrawableAwesome;
import uci.wifiproxy.ui.ui.tabs.PreferencesTab;
import uci.wifiproxy.ui.ui.tabs.UserInfoTab;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String UPDATE_QUOTA_STATE = "update quota state";
    public static final int TABS_NUMBER = 3;
    public static int LIGHT_THEME = 0;
    public static int DARK_THEME = 1;
    public int themeId;

    private UserInfoTab userInfoTab;
    private PreferencesTab preferencesTab;

    private CustomPageAdapter pageAdapter;
    private ViewPager viewPager;
    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chargeTheme();
        setContentView(R.layout.app_bar_main);
        initUi();
        loadConf();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //used to configure the form when it is restarted
        //if closed by the system
        if (isProxyServiceRunning(this)) {
            disableAll();
        } else {
            enableAll();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about_us) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            if (themeId == MainActivity.DARK_THEME) {
                alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AlertDialogCustom)).create();
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void chargeTheme() {
        SharedPreferences settings = getSharedPreferences("UCIntlm.conf",
                Context.MODE_PRIVATE);
        themeId = settings.getInt("theme", LIGHT_THEME);
        if (themeId == LIGHT_THEME) {
            setTheme(R.style.AppTheme_NoActionBar);
        } else if (themeId == DARK_THEME) {
            setTheme(R.style.DarkTheme_NoActionBar);
        }
    }

    private int fetchPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    private void initUi() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(getApplicationContext().getResources().getDrawable(R.mipmap.ic_launcher));
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(new DrawableAwesome(R.string.fa_play, 35, Color.WHITE, false, false, 0, 0, 0, 0, this));

        //this is a very ugly solution for set the icons color
        int iconsColor;
        if (themeId == MainActivity.DARK_THEME) {
            iconsColor = Color.WHITE;
        } else {
            iconsColor = fetchPrimaryColor();
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(new DrawableAwesome(R.string.fa_user, 35,
                iconsColor, false, false, 0, 0, 0, 0, this)));
        tabLayout.addTab(tabLayout.newTab().setIcon(new DrawableAwesome(R.string.fa_wrench, 35,
                iconsColor, false, false, 0, 0, 0, 0, this)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        userInfoTab = new UserInfoTab(this);
        userInfoTab.buttonClean.setTextColor(iconsColor);
//        userInfoTab.buttonViewPass.setTextColor(fetchAccentColor());
        preferencesTab = new PreferencesTab(this);

        pageAdapter = new CustomPageAdapter(TABS_NUMBER);
        pageAdapter.addView(userInfoTab.rootView);
        pageAdapter.addView(preferencesTab.rootView);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(TABS_NUMBER);


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void loadConf() {
        SharedPreferences settings = getSharedPreferences("WifiProxy.conf",
                Context.MODE_PRIVATE);
        userInfoTab.username.setText(settings.getString("username", ""));
        userInfoTab.pass.setText(Encripter.decrypt(settings.getString("password", "")));

        preferencesTab.domain.setText(settings.getString("domain", ""));
        preferencesTab.server.setText(settings.getString("server", ""));
        preferencesTab.inputport.setText(settings.getString("inputport", ""));
        preferencesTab.outputport.setText(settings.getString("outputport", ""));
        preferencesTab.bypass.setText(settings.getString("bypass", ""));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            (preferencesTab.globalCheckBox).setChecked(settings.getBoolean("global_proxy", true));
        }

        preferencesTab.spinnerTheme.setSelection(themeId);

        if (userInfoTab.username.getText().toString().equals("")) {
            userInfoTab.username.requestFocus();
        } else {
            userInfoTab.pass.requestFocus();
        }

        preferencesTab.authSchemeSpinner.setSelection(settings.getInt("authSchemeSelectedPos", 0));
    }

    private void saveConf() {
        SharedPreferences settings = getSharedPreferences("WifiProxy.conf",
                Context.MODE_PRIVATE);
        Editor editor = settings.edit();
        editor.putString("username", userInfoTab.username.getText().toString());
        editor.putString("password",
                Encripter.encrypt(userInfoTab.pass.getText().toString()));

        editor.putString("domain", preferencesTab.domain.getText().toString());
        editor.putString("server", preferencesTab.server.getText().toString());
        editor.putString("inputport", preferencesTab.inputport.getText().toString());
        editor.putString("outputport", preferencesTab.outputport.getText().toString());
        editor.putString("bypass", preferencesTab.bypass.getText().toString());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            editor.putBoolean("global_proxy", (preferencesTab.globalCheckBox).isChecked());
        }

        editor.putInt("authSchemeSelectedPos", preferencesTab.authSchemeSpinner.getSelectedItemPosition());
        editor.apply();
    }


    @SuppressLint("NewApi")
    private void disableAll() {
        //set the form to disable all fields and change the button to stop the service
        userInfoTab.username.setEnabled(false);
        userInfoTab.pass.setEnabled(false);
        preferencesTab.domain.setEnabled(false);
        preferencesTab.server.setEnabled(false);
        preferencesTab.inputport.setEnabled(false);
        preferencesTab.outputport.setEnabled(false);
        preferencesTab.spinnerTheme.setEnabled(false);
        preferencesTab.bypass.setEnabled(false);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            preferencesTab.globalCheckBox.setEnabled(false);
        } else {
            preferencesTab.wiffiSettingsButton.setEnabled(false);
        }

        //TODO: falta para cuando es mayor que LOLLIPOP

        fab.setImageDrawable(new DrawableAwesome(R.string.fa_stop, 35, Color.WHITE, false, false, 0, 0, 0, 0, this));

        //se desabilitan los elementos visuales relativos a la consulta de cuota
        preferencesTab.authSchemeSpinner.setEnabled(false);
    }

    @SuppressLint("NewApi")
    private void enableAll() {
        //set the form to introduce data and start the service
        userInfoTab.username.setEnabled(true);
        userInfoTab.pass.setEnabled(true);
        preferencesTab.domain.setEnabled(true);
        preferencesTab.server.setEnabled(true);
        preferencesTab.inputport.setEnabled(true);
        preferencesTab.outputport.setEnabled(true);
        preferencesTab.spinnerTheme.setEnabled(true);
        preferencesTab.bypass.setEnabled(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            preferencesTab.globalCheckBox.setEnabled(true);
        } else {
            preferencesTab.wiffiSettingsButton.setEnabled(true);
        }

        fab.setImageDrawable(new DrawableAwesome(R.string.fa_play, 35, Color.WHITE, false, false, 0, 0, 0, 0, this));

        preferencesTab.authSchemeSpinner.setEnabled(true);
    }

    public void clickRun(View arg0) {
        viewPager.setCurrentItem(0, true);

        if ((preferencesTab.authSchemeSpinner.getSelectedItemPosition() == 1 && preferencesTab.domain.getText().toString().equals(""))
                || preferencesTab.server.getText().toString().equals("")
                || preferencesTab.inputport.getText().toString().equals("")
                || preferencesTab.outputport.getText().toString().equals("")
                || userInfoTab.username.getText().toString().equals("")
                || userInfoTab.pass.getText().toString().equals("")) {

            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.nodata),
                    Toast.LENGTH_SHORT).show();
            fab.setImageDrawable(new DrawableAwesome(R.string.fa_play, 35, Color.WHITE, false, false, 0, 0, 0, 0, this));
            return;
        }

        if (!isProxyServiceRunning(this)) {
            startProxy();
        } else {
            Intent proxyIntent = new Intent(this, ProxyService.class);
            stopService(proxyIntent);

            enableAll();

//            UCIntlmWidget.actualizarWidget(this.getApplicationContext(),
//                    AppWidgetManager.getInstance(this.getApplicationContext()),
//                    "off");
        }
    }

    public void startProxy() {
        Intent proxyIntent = new Intent(this, ProxyService.class);
        saveConf();
        proxyIntent.putExtra("user", userInfoTab.username.getText().toString());
        proxyIntent.putExtra("pass", userInfoTab.pass.getText().toString());
        proxyIntent.putExtra("domain", preferencesTab.domain.getText().toString());
        proxyIntent.putExtra("server", preferencesTab.server.getText().toString());
        proxyIntent.putExtra("inputport", preferencesTab.inputport.getText().toString());
        proxyIntent.putExtra("outputport", preferencesTab.outputport.getText().toString());
        proxyIntent.putExtra("bypass", preferencesTab.bypass.getText().toString());
        switch (preferencesTab.authSchemeSpinner.getSelectedItemPosition()){
            case 0:
                proxyIntent.putExtra("authScheme", HttpForwarder1.BASIC_SCHEME);
                break;
            case 1:
                proxyIntent.putExtra("authScheme", HttpForwarder1.NTLM_SCHEME);
                break;
            case 2:
                proxyIntent.putExtra("authScheme", HttpForwarder1.DIGEST_SCHEME);
                break;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            proxyIntent.putExtra("set_global_proxy", preferencesTab.globalCheckBox.isChecked());
        } else {
            proxyIntent.putExtra("set_global_proxy", false);
        }

        startService(proxyIntent);
        disableAll();
    }

    private static boolean isProxyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (ProxyService.class.getName().equals(
                    service.service.getClassName())) {
                Log.i(MainActivity.class.getName(), "Service running");
                return true;
            }
        }
        Log.i(MainActivity.class.getName(), "Service not running");
        return false;
    }

}

