package uci.wifiproxy.firewall;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import uci.wifiproxy.data.applicationPackage.ApplicationPackageLocalDataSource;
import uci.wifiproxy.data.firewallRule.FirewallRule;
import uci.wifiproxy.data.firewallRule.FirewallRuleDataSource;
import uci.wifiproxy.data.firewallRule.FirewallRuleLocalDataSource;
import uci.wifiproxy.util.StringUtils;
import uci.wifiproxy.util.network.ClientResolver;
import uci.wifiproxy.util.network.ConnectionDescriptor;

/**
 * Created by daniel on 12/11/17.
 */

public class Firewall {

    private FirewallRuleLocalDataSource mFirewallRuleDataSource;

    public Firewall() {
        mFirewallRuleDataSource = FirewallRuleLocalDataSource.newInstance();
    }

    public void releaseResources(){
        mFirewallRuleDataSource.releaseResources();
    }



}
