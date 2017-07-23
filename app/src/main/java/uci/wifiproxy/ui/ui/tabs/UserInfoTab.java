package uci.wifiproxy.ui.ui.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import uci.wifiproxy.R;
import uci.wifiproxy.ui.ui.fontAwesome.ButtonAwesome;

/**
 * Created by daniel on 23/02/17.
 */

public class UserInfoTab {

    public Context context;
    public View rootView;

    public CheckBox checkBoxShowPassword;
    public EditText username;
    public EditText pass;
    public ButtonAwesome buttonClean;


    public UserInfoTab(Context context) {
        this.context = context;
        this.context = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        rootView = layoutInflater.inflate(R.layout.user_info_tab, null);
        loadUi();
    }

    private void loadUi(){
        username = (EditText) rootView.findViewById(R.id.euser);
        pass = (EditText) rootView.findViewById(R.id.epass);
        checkBoxShowPassword = (CheckBox) rootView.findViewById(R.id.checkBoxPass);
        buttonClean = (ButtonAwesome) rootView.findViewById(R.id.buttonClean);
        buttonClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = context.getSharedPreferences("WifiProxy.conf",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", "");
                editor.putString("password","");
                editor.apply();
                username.setText("");
                pass.setText("");
            }
        });
    }
}
