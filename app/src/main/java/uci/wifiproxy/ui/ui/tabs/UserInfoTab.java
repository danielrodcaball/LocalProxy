package uci.wifiproxy.ui.ui.tabs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import uci.wifiproxy.R;

/**
 * Created by daniel on 23/02/17.
 */

public class UserInfoTab {

    public Context context;
    public View rootView;

    public CheckBox checkBoxShowPassword;
    public EditText username;
    public EditText pass;


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
    }
}
