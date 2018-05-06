package uci.localproxy.util.Security;

import android.annotation.SuppressLint;
import android.os.Build;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;


/* *
 * Esta clase es utilizada para cifrar la contraseña para guardarla.
 * */

@SuppressLint("NewApi")
public class Encripter {
    static private String encriptPass = Build.SERIAL;


    /**
    Password Based Encryption (PBE) is the algorithm used by this methods.
     The strength of its cipher depends on the strength of the secret key
     */

    public static String encrypt(String cadena) {

//        for (Provider provider : Security.getProviders()) {
//            Log.e("Provider", provider.getName());
//            for (Provider.Service service : provider.getServices()) {
//                Log.e("Algorithm", service.getAlgorithm());
//            }
//        }
        StandardPBEStringEncryptor s = new StandardPBEStringEncryptor();
        s.setPassword(encriptPass);
        return s.encrypt(cadena);
    }

    public static String decrypt(String cadena) {
        StandardPBEStringEncryptor s = new StandardPBEStringEncryptor();
        s.setPassword(encriptPass);
        String devuelve = "";
        try {
            devuelve = s.decrypt(cadena);
        } catch (Exception e) {
        }
        return devuelve;
    }
}
