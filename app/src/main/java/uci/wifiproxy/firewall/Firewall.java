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
import uci.wifiproxy.util.StringUtils;
import uci.wifiproxy.util.network.ClientResolver;
import uci.wifiproxy.util.network.ConnectionDescriptor;

/**
 * Created by daniel on 12/11/17.
 */

public class Firewall {

    private List<FirewallRule.FirewallRuleLoaded> mFirewallRulesLoaded;

//    private PackageManager mPackageManager;

    private Context mContext;

    private ClientResolver clientResolver;

    public Firewall(List<FirewallRule.FirewallRuleLoaded> firewallRulesLoaded, Context context) {
        mFirewallRulesLoaded = firewallRulesLoaded;
        mContext = context;
        clientResolver = new ClientResolver(context);
    }

    public boolean filter(int localPort, String localAddress, String uri) {
        ConnectionDescriptor connectionDescriptor = clientResolver.getClientDescriptor(localPort, localAddress);
        String packageNameSource = connectionDescriptor.getNamespace();

//        Log.e("source", packageNameSource);

        for (FirewallRule.FirewallRuleLoaded firewallRuleL : mFirewallRulesLoaded) {
            if (
                    firewallRuleL.applicationPackageName.equals(ApplicationPackageLocalDataSource.ALL_APPLICATION_PACKAGES_STRING)
                            || firewallRuleL.isActive
                            && packageNameSource.equals(firewallRuleL.applicationPackageName)
                            && StringUtils.matches(uri, firewallRuleL.rule)
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
