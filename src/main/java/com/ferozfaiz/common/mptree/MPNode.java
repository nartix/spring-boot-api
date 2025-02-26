package com.ferozfaiz.common.mptree;


import jakarta.persistence.*;

import java.util.List;

/**
 *  @author Feroz Faiz
 * An abstract base entity for materialized path trees.
 * All tree-enabled models should extend this class.
 */
@MappedSuperclass
public abstract class MPNode {

//    @Autowired
//    private transient TreePathService treePathService;

    public static final int STEPLEN = 4; // Fixed width for each path segment
    private static final int DEFAULT_STEP_LENGTH = 4;
    private static final String DEFAULT_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * The materialized path that represents this node's position in the tree.
     * The path is built from fixed-width segments (e.g., 4-character segments).
     * Example: "0001", "00010001", etc.
     */

    @Column(name = "path", nullable = false, unique = true, length = 255)
    private String path;

    /**
     * The depth (level) of the node in the tree.
     * This is calculated as the number of segments in the path.
     */
    @Column(name = "depth", nullable = false)
    private int depth;

    /**
     * The number of direct child nodes.
     */
    @Column(name = "numchild", nullable = false)
    private int numChild;


    // Constructors
    public MPNode() {
        // Default constructor
    }

    public MPNode(String path, int depth, int numChild, String name, String description) {
        this.path = path;
        this.depth = depth;
        this.numChild = numChild;
    }

    // No setter for id as it's generated

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getNumChild() {
        return numChild;
    }

    public void setNumChild(int numChild) {
        this.numChild = numChild;
    }

    // Transient cache for the parent node
    @Transient
    private MPNode cachedParent;

    private String generatePath(String parentPath, int index) {
        String step = String.format("%04d", index);
        return (parentPath == null ? "" : parentPath) + step;
    }


    // Method to add a root node
    public static MPNode addRoot(EntityManager em, MPNode node, TreePathService treePathService) {
        node.setPath(treePathService.getPath(null, 0));
        node.setDepth(0);
        node.setNumChild(0);
        em.persist(node);
        return node;
    }

    /**
     * Retrieves the parent node.
     * If update is true, it bypasses any cached parent and fetches a fresh copy.
     * If this node is a root (depth 1), returns null.
     *
     * @param em     the EntityManager used for the lookup
     * @param update if true, forces a fresh lookup by clearing the cache
     * @return the parent node of type T, or null if this is a root
     */
    @SuppressWarnings("unchecked")
    public MPNode getParent(EntityManager em, Boolean update) {
        if (this.getDepth() <= 1) {
            return null; // Root node has no parent
        }
        if (Boolean.TRUE.equals(update)) {
            cachedParent = null;
        }
        if (cachedParent != null) {
            return cachedParent;
        }
        // Compute parent's path by removing the last fixed-length segment
        String parentPath = this.getPath().substring(0, this.getPath().length() - STEPLEN);
        // Create a typed query using the runtime class, cast safely to Class<T>
        TypedQuery<MPNode> query = em.createQuery(
                "SELECT n FROM " + this.getClass().getSimpleName() + " n WHERE n.path = :parentPath",
                (Class<MPNode>) this.getClass());
        query.setParameter("parentPath", parentPath);
        List<MPNode> result = query.getResultList();
        if (!result.isEmpty()) {
            cachedParent = result.get(0);
        }
        return cachedParent;
    }

}



