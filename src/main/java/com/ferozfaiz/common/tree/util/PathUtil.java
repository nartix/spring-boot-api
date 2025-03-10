package com.ferozfaiz.common.tree.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Feroz Faiz
 */
public class PathUtil {
    private NumConvInterface numConv;
    private int stepLength;
    private static final Map<String, NumConv> CACHE = new ConcurrentHashMap<>();

    public PathUtil(NumConv numConv, int stepLength) {
        if (numConv == null) {
            throw new IllegalArgumentException("numConv cannot be null");
        }
        if (stepLength < 1) {
            throw new IllegalArgumentException("stepLength must be greater than 0");
        }
        this.numConv = numConv;
        this.stepLength = stepLength;
    }

    public PathUtil(NumConv numConv) {
        this(numConv, 4);
    }

    public PathUtil(int stepLength) {
        this(new NumConv(36, NumConv.BASE36), stepLength);
    }

    public PathUtil(int radix, String alphabet, int stepLength) {
        this(new NumConv(radix, alphabet), stepLength);
    }

    public PathUtil(int radix, String alphabet) {
        this(new NumConv(radix, alphabet), 4);
    }

    public PathUtil() {
        this.numConv = new NumConv(36, NumConv.BASE36);
        this.stepLength = 4;
    }

    public NumConvInterface getNumConv() {
        return numConv;
    }

    private static NumConv getCachedNumConv(int radix, String alphabet) {
        String key = radix + ":" + alphabet;
        return CACHE.computeIfAbsent(key, k -> new NumConv(radix, alphabet));
    }

    public String intToStr(long num) {
        return getNumConv().intToStr(num);
    }

    public long strToInt(String str) {
        return getNumConv().strToInt(str);
    }

    public String intToStr(long num, int radix) {
        return getCachedNumConv(radix, NumConv.BASE36).intToStr(num);
    }

    public String intToStr(long num, int radix, String alphabet) {
        return getCachedNumConv(radix, alphabet).intToStr(num);
    }

    public long strToInt(String num, int radix) {
        return getCachedNumConv(radix, NumConv.BASE36).strToInt(num);
    }

    public long strToInt(String num, int radix, String alphabet) {
        return getCachedNumConv(radix, alphabet).strToInt(num);
    }

    public String getAlphabet() {
        return getNumConv().getAlphabet();
    }

    public String getBasePath(String path, int depth) {
        if (path != null && !path.isEmpty()) {
            return path.substring(0, Math.min(depth * stepLength, path.length()));
        }
        return "";
    }

    public String getPath(String path, int depth, long newstep) {
        String parentPath = getBasePath(path, depth - 1);
        String key = getNumConv().intToStr(newstep);

        // Pad the key with '0's to ensure a fixed length of STEP_LEN
        int padLength = stepLength - key.length();
        StringBuilder pad = new StringBuilder();
        for (int i = 0; i < padLength; i++) {
            pad.append(getNumConv().getAlphabet().charAt(0));
        }
        return parentPath + pad.toString() + key;
    }

    public int getDepth(String path) {
        if (path == null || path.isEmpty()) return 0;
        if (path.length() % stepLength != 0) {
            throw new IllegalArgumentException("Path length is not a multiple of stepLength");
        }
        return path.length() / stepLength;
    }

    public boolean isAncestor(String ancestorPath, String descendantPath) {
        if (ancestorPath == null || descendantPath == null) return false;
        if (descendantPath.length() <= ancestorPath.length()) return false;
        if (!descendantPath.startsWith(ancestorPath)) return false;
        // Ancestor must align to whole segment (fixed-length) boundaries
        return (descendantPath.length() - ancestorPath.length()) % stepLength == 0;
    }

    public String rebasePath(String oldPath, String newParentPath) {
        if (oldPath == null || oldPath.isEmpty()) {
            throw new IllegalArgumentException("oldPath cannot be null or empty");
        }
        String lastSegment = oldPath.substring(oldPath.length() - stepLength);
        return (newParentPath == null || newParentPath.isEmpty())
                ? lastSegment
                : newParentPath + lastSegment;
    }

    public int getStepLength() {
        return stepLength;
    }

    public long getMaxStepLength() {
        return (long) Math.pow(getNumConv().getRadix(), stepLength);
    }

    public String getFirstAncestor(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        return path.substring(0, getStepLength());
    }

    public String getPathByDepth(String path, int depth) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (depth < 1) {
            throw new IllegalArgumentException("Depth must be greater than 0");
        }
        int stepLength = getStepLength();
        int pathEndLength = depth * stepLength;
        if (path.length() < pathEndLength) {
            throw new IllegalArgumentException("Path length is less than the expected step length");
        }
        return path.substring(pathEndLength - getStepLength(), pathEndLength);
    }
}
