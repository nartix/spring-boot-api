package com.ferozfaiz.common.tree.util;

import java.util.HashMap;
import java.util.Map;
/**
 * @author Feroz Faiz
 * Ported from the Python version of the code.
 * <a href="https://github.com/django-treebeard/django-treebeard/blob/master/treebeard/numconv.py">NumConv</a>
 */
public class NumConv implements NumConvInterface {
    public static final String BASE85 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!#$%&()*+-;<=>?@^_`{|}~";
    public static final String BASE16 = BASE85.substring(0, 16);
    public static final String BASE32 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    public static final String BASE36 = BASE85.substring(0, 36);
    public static final String BASE32HEX = BASE85.substring(0, 32);
    public static final String BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    public static final String BASE64URL = BASE64.substring(0, 62) + "-_";
    public static final String BASE62 = BASE85.substring(0, 62);

    private final int radix;
    private final String alphabet;
    private final Map<Character, Integer> cachedMap;

    public NumConv(int radix, String alphabet) {
        if (radix < 2 || radix > alphabet.length()) {
            throw new IllegalArgumentException("Radix out of range.");
        }
        this.radix = radix;
        this.alphabet = alphabet;
        // Build cachedMap and check for duplicate characters.
        this.cachedMap = new HashMap<>();
        for (int i = 0; i < alphabet.length(); i++) {
            char c = alphabet.charAt(i);
            if (cachedMap.containsKey(c)) {
                throw new IllegalArgumentException("Duplicate character found in alphabet: " + c);
            }
            cachedMap.put(c, i);
        }
    }

    public NumConv() {
        this(10, BASE85);
    }

    public NumConv(int radix) {
        this(radix, BASE85);
    }

    public String intToStr(long num) {
        if (num < 0) {
            throw new IllegalArgumentException("Number must be non-negative.");
        }
        // Use built-in conversion for common radices if the alphabet matches.
        if ((radix == 8 || radix == 10 || radix == 16) &&
                alphabet.substring(0, radix).equalsIgnoreCase(BASE85.substring(0, radix))) {
            switch (radix) {
                case 8:
                    return Long.toOctalString(num).toUpperCase();
                case 10:
                    return Long.toString(num).toUpperCase();
                case 16:
                    return Long.toHexString(num).toUpperCase();
            }
        }
        StringBuilder result = new StringBuilder();
        do {
            int remainder = (int) (num % radix);
            result.append(alphabet.charAt(remainder));
            num /= radix;
        } while (num > 0);
        return result.reverse().toString();
    }

    /**
     * Converts a string into a long value using the configured radix and alphabet.
     * <p>
     * If the radix is less than or equal to 36 and the alphabet's prefix matches the
     * default BASE85 prefix (ignoring case), the built-in Java parsing is used.
     * Otherwise, each character is looked up in the cached map.
     * </p>
     *
     * @param num the string representation of the number.
     * @return the corresponding long value.
     * @throws IllegalArgumentException if the string contains invalid characters.
     */
    public long strToInt(String num) {
        // Use built-in conversion when possible.
        if (radix <= 36 && alphabet.substring(0, radix).equalsIgnoreCase(BASE85.substring(0, radix))) {
            try {
                return Long.parseLong(num, radix);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        java.lang.String.format("Invalid literal for strToInt() with radix %d: '%s'", radix, num), e);
            }
        }
        long ret = 0;
        // Convert each character using the cached map.
        for (int i = 0; i < num.length(); i++) {
            char c = num.charAt(i);
            Integer value = cachedMap.get(c);
            if (value == null || value >= radix) {
                throw new IllegalArgumentException(
                        java.lang.String.format("Invalid literal for strToInt() with radix %d: '%s'", radix, num));
            }
            ret = ret * radix + value;
        }
        return ret;
    }

    public String getAlphabet() {
        return alphabet;
    }

    public int getRadix() {
        return radix;
    }
}