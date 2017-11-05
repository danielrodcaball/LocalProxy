package uci.wifiproxy.util;

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

}
