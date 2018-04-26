package uci.wifiproxy.util.network;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by daniel on 25/04/18.
 */

public class PortsUtils {

    public static boolean isPortAvailable(int port){
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                /* should not be thrown */
                }
            }
        }

        return false;
    }

    public static boolean isPortAvailable1(int port){
        Socket s = null;
        try {
            s = new Socket("localhost", port);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (s != null) try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
