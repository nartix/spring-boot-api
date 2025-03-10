package com.ferozfaiz.common.tree.materializedpath;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * @author Feroz Faiz
 */
@MappedSuperclass
public abstract class MaterializedPathNode<T> {

    /**
     * The materialized path that represents this node's position in the tree.
     * The path is built from fixed-width segments (e.g., 4-character segments).
     * Example: "0001", "00010001", etc.
     */
    @Column(name = "path", nullable = false, unique = true, length = 255)
    private String path;

    public static int getPathColumnLength() {
        return 255;
    }

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

    public MaterializedPathNode() {
    }

    public MaterializedPathNode(String path, int depth, int numChild, String name) {
        this.path = path;
        this.depth = depth;
        this.numChild = numChild;
    }

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
}
