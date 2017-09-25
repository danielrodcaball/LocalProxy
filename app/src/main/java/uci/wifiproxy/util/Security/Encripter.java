package uci.wifiproxy.util.Security;

import android.annotation.SuppressLint;
import android.os.Build;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;


/* *
 * Esta clase es utilizada para cifrar la contraseña para guardarla.
 * */

@SuppressLint("NewApi")
public class Encripter {
    static private String encriptPass = Build.SERIAL;

    public static String encrypt(String cadena) {
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
