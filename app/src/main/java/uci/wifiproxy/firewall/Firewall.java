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

//    private PackageManager mPackageManager;

    private Context mContext;

    private FirewallRuleLocalDataSource mFirewallRuleDataSource;

    private ClientResolver clientResolver;

    public Firewall(Context context) {
        mContext = context;
        mFirewallRuleDataSource = FirewallRuleLocalDataSource.newInstance();
        clientResolver = new ClientResolver(context);
    }

    public void releaseResources(){
        mFirewallRuleDataSource.releaseResources();
    }

    public boolean filter(int localPort, String localAddress, String uri) {
        ConnectionDescriptor connectionDescriptor = clientResolver.getClientDescriptor(localPort, localAddress);
        String packageNameSource = connectionDescriptor.getNamespace();

//        Log.e("source", packageNameSource);

        for (FirewallRule firewallRule: mFirewallRuleDataSource.getActiveFirewallRules()) {
            if (
                    (firewallRule.getApplicationPackageName().equals(ApplicationPackageLocalDataSource.ALL_APPLICATION_PACKAGES_STRING)
                            && StringUtils.matches(uri, firewallRule.getRule()))
                            || (packageNameSource.equals(firewallRule.getApplicationPackageName())
                            && StringUtils.matches(uri, firewallRule.getRule()))
                    ) {
                Log.i(getClass().getName(), packageNameSource + " : " + uri + " blocked by firewall");
                return false;
            }
        }

        Log.i(getClass().getName(), packageNameSource + " : " + uri + " pass the firewall");
        return true;
    }

    private String getPackageNameByLocalPort(int port) {
        try {
            BufferedReader bf = new BufferedReader(new FileReader("/proc/net/tcp"));
            String line;
            Log.e("localPort", port + "");

            while ((line = bf.readLine()) != null) {
                Log.e("Line", line);
                line = line.trim();
                String[] arr = line.split("\\s");

                if (arr[0].equals("sl")) continue;

                String localPortHex = arr[1].split(":")[1];
                String uid = arr[7];

                String portHex = Integer.toHexString(port);
//                Log.e("portHex", portHex);
                if (portHex.equalsIgnoreCase(localPortHex)) {
                    return mContext.getPackageManager().getNameForUid(Integer.parseInt(uid));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
