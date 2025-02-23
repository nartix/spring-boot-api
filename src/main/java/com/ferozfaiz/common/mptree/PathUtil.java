package com.ferozfaiz.common.mptree;

/**
 * @author Feroz Faiz
 */
public class PathUtil {

    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int STEP_LENGTH = 4;
    private static final int BASE = ALPHABET.length();

    public static String encode(int number, String alphabet, int length) {
        StringBuilder result = new StringBuilder();
        int base = alphabet.length();
        while (number > 0) {
            result.append(alphabet.charAt(number % base));
            number /= base;
        }
        while (result.length() < length) {
            result.insert(0, alphabet.charAt(0));
        }
        return result.reverse().toString();
    }

    public static String encode2(int number, String alphabet, int steplen) {
        StringBuilder result = new StringBuilder();
        int base = alphabet.length();

        // Convert the number to the given base
        while (number > 0) {
            result.append(alphabet.charAt(number % base));
            number /= base;
        }

        // Pad the result to ensure it matches the fixed step length
        while (result.length() < steplen) {
            result.append(alphabet.charAt(0));
        }

        // Reverse to get the correct order
        return result.reverse().toString();
    }

    public static String intToBase62(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Number must be non-negative.");
        }
        StringBuilder result = new StringBuilder();
        do {
            int remainder = number % BASE;
            result.append(ALPHABET.charAt(remainder));
            number /= BASE;
        } while (number > 0);
        return result.reverse().toString();
    }

    // todo: throw exception if num greater than alphabet.length()^STEP_LEN
    public static String intToStr(int num, String alphabet) {
        if (num == 0) return "0";
        int base = alphabet.length();
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int remainder = num % base;
            sb.insert(0, alphabet.charAt(remainder));
            num /= base;
        }
        return sb.toString();
    }

    /**
     * Converts a Base62 encoded string back to an integer.
     *
     * @param base62Str the Base62 encoded string
     * @return the decoded integer
     */
    public static int base62ToInt(String base62Str) {
        int result = 0;
        for (char character : base62Str.toCharArray()) {
            result = result * BASE + ALPHABET.indexOf(character);
        }
        return result;
    }

    public static String getPath(String parentPath, int stepNumber) {
        String encodedStep = intToBase62(stepNumber);
        // Pad the encoded step to ensure it matches the STEP_LENGTH
        String paddedStep = String.format("%" + STEP_LENGTH + "s", encodedStep).replace(' ', ALPHABET.charAt(0));
        return parentPath + paddedStep;
    }

    public static String getPath(String basePath, int depth, int newStep) {
        String parentPath = "";
        if (basePath != null && !basePath.isEmpty() && depth > 1) {
            int subLength = (depth - 1) * STEP_LENGTH;
            // Ensure that the parent's path is long enough
            if (basePath.length() >= subLength) {
                parentPath = basePath.substring(0, subLength);
            }
        }
        String key = intToStr(newStep, ALPHABET);
        // Pad the key with '0's to ensure a fixed length of STEP_LEN
        int padLength = STEP_LENGTH - key.length();
        StringBuilder pad = new StringBuilder();
        for (int i = 0; i < padLength; i++) {
            pad.append(ALPHABET.charAt(0));
        }
        return parentPath + key + pad.toString();
    }
}
