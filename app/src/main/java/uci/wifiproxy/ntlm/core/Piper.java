package uci.wifiproxy.ntlm.core;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * Tubería para vincular el flujo de entrada y el de salida
 * */

public class Piper
        implements Runnable {

    InputStream is;
    OutputStream os;
    byte[] buffer = new byte[1500];
    int read;

    public Piper(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
    }

    public Piper() {
    }

    public void run() {
        try {
            while (true) {
                int read = this.is.read(this.buffer);

                if (read == -1) {
                    break;
                }
                this.os.write(this.buffer, 0, read);
            }
        } catch (IOException e) {
            Log.i("ERROR", e.getMessage());
        }
        close();
    }

    public InputStream getIs() {
        return this.is;
    }

    public void setIs(InputStream is) {
        this.is = is;
    }

    public OutputStream getOs() {
        return this.os;
    }

    public void setOs(OutputStream os) {
        this.os = os;
    }

    public void close() {
        try {
            this.is.close();
        } catch (Exception ex) {

        }
        try {
            this.os.close();
        } catch (Exception ex) {

        }
    }
}
