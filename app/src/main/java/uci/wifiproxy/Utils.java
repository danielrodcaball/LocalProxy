package uci.wifiproxy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/**
 * Created by daniel on 18/02/17.
 */

public class Utils {

    public static void enableSSLSocket() throws KeyManagementException, NoSuchAlgorithmException {

        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new X509TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }

    //String fileName = context.getFilesDir()+File.separator+usuario.png
    public static boolean savePictureInFile(String fileName, Bitmap pictureBitmap) {
        try {
            File directory = new File(fileName.substring(0, fileName.lastIndexOf(File.separator)));
            if (directory.exists() || directory.mkdirs()) {
                File file = new File(fileName); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                OutputStream fOut = new FileOutputStream(file);
                pictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                fOut.flush(); // Not really required
                fOut.close(); // do not forget to close the stream
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Bitmap loadPictureInFile(String filename) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(filename, options);
        return bitmap;
    }

    public static Object getField(Object obj, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    public static Object getDeclaredField(Object obj, String name)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    public static void setEnumField(Object obj, String value, String name)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }

    public static void setProxySettings(String assign, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        setEnumField(wifiConf, assign, "proxySettings");
    }


    private static WifiConfiguration GetCurrentWifiConfiguration(WifiManager manager) {
        if (!manager.isWifiEnabled())
            return null;

        List<WifiConfiguration> configurationList = manager.getConfiguredNetworks();
        WifiConfiguration configuration = null;
        int cur = manager.getConnectionInfo().getNetworkId();
        for (int i = 0; i < configurationList.size(); ++i) {
            WifiConfiguration wifiConfiguration = configurationList.get(i);
            if (wifiConfiguration.networkId == cur)
                configuration = wifiConfiguration;
        }

        return configuration;
    }

    public static void setWifiProxySettings(Context context, int outputport, String bypass) {
        //get the current wifi configuration
        WifiManager manager;
        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration config = GetCurrentWifiConfiguration(manager);


        if (null == config)
            return;

        try {
            //get the link properties from the wifi configuration
            Object linkProperties = getField(config, "linkProperties");
            if (null == linkProperties)
                return;

            //get the setHttpProxy method for LinkProperties
            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            Class[] setHttpProxyParams = new Class[1];
            setHttpProxyParams[0] = proxyPropertiesClass;
            Class lpClass = Class.forName("android.net.LinkProperties");
            Method setHttpProxy = lpClass.getDeclaredMethod("setHttpProxy", setHttpProxyParams);
            setHttpProxy.setAccessible(true);

            //get ProxyProperties constructor
            Class[] proxyPropertiesCtorParamTypes = new Class[3];
            proxyPropertiesCtorParamTypes[0] = String.class;
            proxyPropertiesCtorParamTypes[1] = int.class;
            proxyPropertiesCtorParamTypes[2] = String.class;

            Constructor proxyPropertiesCtor = proxyPropertiesClass.getConstructor(proxyPropertiesCtorParamTypes);

            //create the parameters for the constructor
            Object[] proxyPropertiesCtorParams = new Object[3];
            proxyPropertiesCtorParams[0] = "127.0.0.1";
            proxyPropertiesCtorParams[1] = outputport;
            proxyPropertiesCtorParams[2] = bypass;

            //create a new object using the params
            Object proxySettings = proxyPropertiesCtor.newInstance(proxyPropertiesCtorParams);

            //pass the new object to setHttpProxy
            Object[] params = new Object[1];
            params[0] = proxySettings;
            setHttpProxy.invoke(linkProperties, params);

            setProxySettings("STATIC", config);

            //save the settings
            manager.updateNetwork(config);
            manager.disconnect();
            manager.reconnect();
            Log.i("Proxy details after set", getUserProxy(context)[0] + ":" + getUserProxy(context)[1]);
        } catch (Exception e) {
        }
    }

    public static void unsetWifiProxySettings(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration config = GetCurrentWifiConfiguration(manager);
        if (null == config)
            return;

        try {
            //get the link properties from the wifi configuration
            Object linkProperties = getField(config, "linkProperties");
            if (null == linkProperties)
                return;

            //get the setHttpProxy method for LinkProperties
            Class proxyPropertiesClass = Class.forName("android.net.ProxyProperties");
            Class[] setHttpProxyParams = new Class[1];
            setHttpProxyParams[0] = proxyPropertiesClass;
            Class lpClass = Class.forName("android.net.LinkProperties");
            Method setHttpProxy = lpClass.getDeclaredMethod("setHttpProxy", setHttpProxyParams);
            setHttpProxy.setAccessible(true);

            //pass null as the proxy
            Object[] params = new Object[1];
            params[0] = null;
            setHttpProxy.invoke(linkProperties, params);

            setProxySettings("NONE", config);

            //save the config
            manager.updateNetwork(config);
            manager.disconnect();
            manager.reconnect();
            Log.i("Proxy details after unset", getUserProxy(context)[0] + ":" + getUserProxy(context)[1]);
        } catch (Exception e) {
        }
    }

    private static boolean IsPreIcs() {
        return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH);
    }

    public static String getProxyDetails(Context context) {
        String proxyAddress = new String();
        try {
            if (IsPreIcs()) {
                proxyAddress = android.net.Proxy.getHost(context);
                if (proxyAddress == null || proxyAddress.equals("")) {
                    return proxyAddress;
                }
                proxyAddress += ":" + android.net.Proxy.getPort(context);
            } else {
                proxyAddress = System.getProperty("http.proxyHost");
                proxyAddress += ":" + System.getProperty("http.proxyPort");
            }
        } catch (Exception ex) {
            //ignore
        }
        return proxyAddress;
    }

    public static String[] getUserProxy(Context context) {
        Method method = null;
        try {
            method = ConnectivityManager.class.getMethod("getProxy");
        } catch (NoSuchMethodException e) {
            // Normal situation for pre-ICS devices
            return null;
        } catch (Exception e) {
            return null;
        }

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Object pp = method.invoke(connectivityManager);
            if (pp == null)
                return null;

            return getUserProxy(pp);
        } catch (Exception e) {
            return null;
        }
    }


    private static String[] getUserProxy(Object pp) throws Exception {
        String[] userProxy = new String[3];

        String className = "android.net.ProxyProperties";
        Class<?> c = Class.forName(className);
        Method method;

        method = c.getMethod("getHost");
        userProxy[0] = (String) method.invoke(pp);

        method = c.getMethod("getPort");
        userProxy[1] = String.valueOf((Integer) method.invoke(pp));


        method = c.getMethod("getExclusionList");
        userProxy[2] = (String) method.invoke(pp);

        if (userProxy[0] != null)
            return userProxy;
        else
            return null;
    }
/******************************************************************************/


}
