package com.ferozfaiz.common.tree.materializedpath;

import com.ferozfaiz.common.tree.util.NumConv;
import com.ferozfaiz.common.tree.util.NumConvInterface;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Feroz Faiz
 */
public abstract class MaterializedPathService<T extends MaterializedPathNode<T>, ID > {
    private MaterializedPathRepository<T, ID> repository;
    private final NumConvInterface numConv;
    private int STEPLENGTH;
    private static final Map<String, NumConv> CACHE = new ConcurrentHashMap<>();

    // Primary constructor that takes the repository along with other dependencies.
    public MaterializedPathService(MaterializedPathRepository<T, ID> repository, NumConvInterface numConv, int stepLength) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        if (numConv == null) {
            throw new IllegalArgumentException("numConv cannot be null");
        }
        if (stepLength < 1) {
            throw new IllegalArgumentException("stepLength must be greater than 0");
        }
        this.repository = repository;
        this.numConv = numConv;
        this.STEPLENGTH = stepLength;
    }

    // Additional constructors as needed
    public MaterializedPathService(MaterializedPathRepository<T, ID> repository, NumConvInterface numConv) {
        this(repository, numConv, 4);
    }

    public MaterializedPathService(MaterializedPathRepository<T, ID> repository, int stepLength) {
        this(repository, new NumConv(36, NumConv.BASE36), stepLength);
    }

    public MaterializedPathService(MaterializedPathRepository<T, ID> repository) {
        this(repository, new NumConv(36, NumConv.BASE36), 4);
    }


    public MaterializedPathService(NumConvInterface numConv, int stepLength) {
        if (numConv == null ) {
            throw new IllegalArgumentException("numConv cannot be null");
        }
        if (stepLength < 1) {
            throw new IllegalArgumentException("stepLength must be greater than 0");
        }
        this.numConv = numConv;
        this.STEPLENGTH = stepLength;
    }

    public MaterializedPathService(NumConvInterface numConv) {
        this(numConv, 4);
    }

    public MaterializedPathService(int stepLength) {
        this(new NumConv(36, NumConv.BASE36), stepLength);
    }

    public MaterializedPathService() {
        this(new NumConv(36, NumConv.BASE36), 4);
    }

    public NumConvInterface getNumConv() {
        return numConv;
    }

    public String intToStr(long num) {
        return getNumConv().intToStr(num);
    }

    public long strToInt(String str) {
        return getNumConv().strToInt(str);
    }

    private static NumConv getCachedNumConv(int radix, String alphabet) {
        String key = radix + ":" + alphabet;
        return CACHE.computeIfAbsent(key, k -> new NumConv(radix, alphabet));
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
            return path.substring(0, Math.min(depth * STEPLENGTH, path.length()));
        }
        return "";
    }

    public String getPath(String path, int depth, int newstep) {
        String parentPath = getBasePath(path, depth - 1);
        String key = getNumConv().intToStr(newstep);

        // Pad the key with '0's to ensure a fixed length of STEP_LEN
        int padLength = STEPLENGTH - key.length();
        StringBuilder pad = new StringBuilder();
        for (int i = 0; i < padLength; i++) {
            pad.append(getNumConv().getAlphabet().charAt(0));
        }
        return parentPath + pad.toString() + key;
    }

//    public  String getPath(String basePath, int depth, int newStep) {
//        String parentPath = "";
//        if (basePath != null && !basePath.isEmpty() && depth > 1) {
//            int subLength = (depth - 1) * STEPLENGTH;
//            // Ensure that the parent's path is long enough
//            if (basePath.length() >= subLength) {
//                parentPath = basePath.substring(0, subLength);
//            }
//        }
//        String key = getNumConv().intToStr(newStep);
//        // Pad the key with '0's to ensure a fixed length of STEP_LEN
//        int padLength = STEPLENGTH - key.length();
//        StringBuilder pad = new StringBuilder();
//        for (int i = 0; i < padLength; i++) {
//            pad.append(getNumConv().getAlphabet().charAt(0));
//        }
//        return parentPath +  pad.toString() + key;
//    }

    public int getDepth(String path) {
        if (path == null || path.isEmpty()) return 0;
        if (path.length() % STEPLENGTH != 0) {
            throw new IllegalArgumentException("Path length is not a multiple of stepLength");
        }
        return path.length() / STEPLENGTH;
    }

    public boolean isAncestor(String ancestorPath, String descendantPath) {
        if (ancestorPath == null || descendantPath == null) return false;
        if (descendantPath.length() <= ancestorPath.length()) return false;
        if (!descendantPath.startsWith(ancestorPath)) return false;
        // Ancestor must align to whole segment (fixed-length) boundaries
        return (descendantPath.length() - ancestorPath.length()) % STEPLENGTH == 0;
    }

    public String rebasePath(String oldPath, String newParentPath) {
        if (oldPath == null || oldPath.isEmpty()) {
            throw new IllegalArgumentException("oldPath cannot be null or empty");
        }
        String lastSegment = oldPath.substring(oldPath.length() - STEPLENGTH);
        return (newParentPath == null || newParentPath.isEmpty())
                ? lastSegment
                : newParentPath + lastSegment;
    }

    public T addRoot(T node) {
        // Query existing root nodes (depth = 1) to get the highest path value.
//        TypedQuery<String> query = em.createQuery(
//                "SELECT n.path FROM " + node.getClass().getSimpleName() + " n WHERE n.depth = 1 ORDER BY n.path DESC",
//                String.class);
//        query.setMaxResults(1);
//        List<String> result = query.getResultList();
//
//        String newPath;
//        if (result.isEmpty()) {
//            newPath = formatSegment(1);
//        } else {
//            int lastValue = Integer.parseInt(result.get(0));
//            newPath = formatSegment(lastValue + 1);
//        }
//        node.setPath(newPath);
//        node.setDepth(1);
//        node.setNumChild(0);
//        em.persist(node);
        return node;
    }
}
