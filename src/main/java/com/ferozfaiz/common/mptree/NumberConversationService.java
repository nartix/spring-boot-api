package com.ferozfaiz.common.mptree;

import org.springframework.stereotype.Service;

/**
 * @author Feroz Faiz
 */

@Service
public class NumberConversationService {

    private final NumConv numConv;

    /**
     * Default constructor using radix 10 and the default BASE85 alphabet.
     */
    public NumberConversationService() {
        this.numConv = new NumConv(10, NumConv.BASE85);
    }

    /**
     * Alternative constructor to create a service with a custom radix and alphabet.
     *
     * @param radix    The base to use for conversion.
     * @param alphabet The alphabet string to encode digits.
     */
    public NumberConversationService(int radix, String alphabet) {
        this.numConv = new NumConv(radix, alphabet);
    }

    /**
     * Converts a non-negative long number to its string representation using the configured base.
     *
     * @param num The number to convert (must be non-negative).
     * @return The string representation.
     * @throws IllegalArgumentException if num is negative.
     */
    public String intToStr(long num) {
        return numConv.intToStr(num);
    }

    /**
     * Inner class implementing the conversion logic.
     */
    public static class NumConv {

        // Default BASE85 alphabet as defined in the Python version.
        public static final String BASE85 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!#$%&()*+-;<=>?@^_`{|}~";

        private final int radix;
        private final String alphabet;

        /**
         * Creates a new NumConv instance with the specified radix and alphabet.
         *
         * @param radix    The base for conversion (must be between 2 and alphabet.length()).
         * @param alphabet The string containing digit symbols.
         * @throws IllegalArgumentException if the radix is out of range or if the alphabet has duplicate characters.
         */
        public NumConv(int radix, String alphabet) {
            if (radix < 2 || radix > alphabet.length()) {
                throw new IllegalArgumentException("radix must be >= 2 and <= " + alphabet.length());
            }
            // Check for duplicate characters in the alphabet.
            for (int i = 0; i < alphabet.length(); i++) {
                char c = alphabet.charAt(i);
                if (alphabet.indexOf(c) != i) {
                    throw new IllegalArgumentException("Duplicate character found in alphabet: " + c);
                }
            }
            this.radix = radix;
            this.alphabet = alphabet;
        }

        /**
         * Converts a non-negative long number into a string in the defined radix.
         *
         * @param num The number to convert.
         * @return The string representation of the number.
         * @throws IllegalArgumentException if the number is negative.
         */
        public String intToStr(long num) {
            if (num < 0) {
                throw new IllegalArgumentException("number must be positive");
            }

            // Use built-in conversion if radix is 8, 10, or 16 and the provided alphabet
            // matches the default BASE85 prefix (ignoring case).
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

            // Custom conversion.
            if (num == 0) {
                return String.valueOf(alphabet.charAt(0));
            }
            StringBuilder sb = new StringBuilder();
            while (num > 0) {
                int remainder = (int) (num % radix);
                sb.append(alphabet.charAt(remainder));
                num /= radix;
            }
            return sb.reverse().toString();
        }
    }
}
