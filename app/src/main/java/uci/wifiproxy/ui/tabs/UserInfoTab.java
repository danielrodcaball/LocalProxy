package uci.wifiproxy.ui.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import uci.wifiproxy.R;
import uci.wifiproxy.util.fontAwesome.ButtonAwesome;


public class UserInfoTab {

    Context context;
    public View rootView;

    public EditText username;
    public EditText pass;
    public ButtonAwesome buttonClean;
    ButtonAwesome buttonViewPass;


    public UserInfoTab(Context context) {
        this.context = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        rootView = layoutInflater.inflate(R.layout.user_info_tab, null);
        loadUi();
    }

    private void loadUi(){
        username = (EditText) rootView.findViewById(R.id.euser);
        pass = (EditText) rootView.findViewById(R.id.epass);
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
        buttonViewPass = (ButtonAwesome) rootView.findViewById(R.id.buttonViewPass);
        buttonViewPass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP){
                    pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                return false;
            }
        });
    }
}
