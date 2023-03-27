package mod.tjt01.lapislib.util;

public class ColorCodec {
    public static int decode(String value) throws NumberFormatException {
        if (value.startsWith("#"))
            value = (value.substring(1));
        if (value.length() != 6 && value.length() != 8)
            throw new NumberFormatException("Value must be either 6 or 8 characters long, not counting leading #");

        long x = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            int digit = Character.digit(c, 16);
            if (digit < 0) throw new NumberFormatException("Invalid digit " + c);
            x = x*16 + digit;
        }
        return (int) x;
    }

    public static String encodeRGB(int value, boolean leadingHash) {
        return String.format(leadingHash ? "#%06x" : "%06x", value & 0xFFFFFF);
    }

    public static String encodeARGB(int value, boolean leadingHash) {
        return String.format(leadingHash ? "#%08x" : "%08x", value);
    }
}
