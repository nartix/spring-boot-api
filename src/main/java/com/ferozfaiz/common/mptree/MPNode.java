package com.ferozfaiz.common.mptree;


import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.MappedSuperclass;

/**
 *  @author Feroz Faiz
 * An abstract base entity for materialized path trees.
 * All tree-enabled models should extend this class.
 */
@MappedSuperclass
public abstract class MPNode<T> {

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
    @Column(name = "numchild", nullable = false, columnDefinition = "int default 0")
    private int numChild;

    public MPNode() {
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

    private String generatePath(String parentPath, int index) {
        String step = String.format("%04d", index);
        return (parentPath == null ? "" : parentPath) + step;
    }

    public Object getParent(EntityManager em, boolean b) {
        return null;
    }
}



