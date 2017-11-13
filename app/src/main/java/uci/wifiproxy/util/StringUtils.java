package uci.wifiproxy.util;

import android.util.Log;

import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * Created by daniel on 3/10/17.
 */

public class StringUtils {
    /**
     * Reverse the split operation.
     *
     * @param parts    The parts to combine
     * @param index    the index to the fist part to use
     * @param length   the number of parts to use
     * @param splitter The between-parts text
     */
    public static String unsplit(String[] parts, int index, int length, String splitter) {
        if (parts == null) return null;
        if ((index < 0) || (index >= parts.length)) return null;
        if (index + length > parts.length) return null;

        StringBuilder buf = new StringBuilder();
        for (int i = index; i < index + length; i++) {
            if (parts[i] != null) buf.append(parts[i]);
            buf.append(splitter);
        }

        // remove the trailing splitter
        buf.setLength(buf.length() - splitter.length());
        return buf.toString();
    }

    public static String decToHex(int dec){
        int sizeOfIntInHalfBytes = 4;
        int numberOfBitsInAHalfByte = 4;
        int halfByte = 0x0F;
        char[] hexDigits = {
                '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        StringBuilder hexBuilder = new StringBuilder(sizeOfIntInHalfBytes);
        hexBuilder.setLength(sizeOfIntInHalfBytes);
        for (int i = sizeOfIntInHalfBytes - 1; i >= 0; --i)
        {
            int j = dec & halfByte;
            hexBuilder.setCharAt(i, hexDigits[j]);
            dec >>= numberOfBitsInAHalfByte;
        }
        return hexBuilder.toString();
    }

    public static boolean matches(String url, String bypass) {
        LinkedList<StringBuilder> patterns = new LinkedList<StringBuilder>();

        for (String i : bypass.split(",")) {
            StringBuilder s = new StringBuilder(i);
            if (i.length() > 0) {
                while (s.charAt(0) == ' ') {
                    s.delete(0, 1);
                }
                if (s.charAt(0) == '*') {
                    s.insert(0, ' ');
                }
                patterns.add(s);
            }
        }

        for (StringBuilder i : patterns) {
            Pattern p = Pattern.compile(i.toString());
            if (p.matcher(url).find()) {
                return true;
            }
        }
        return false;
    }

}
