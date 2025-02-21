package com.ferozfaiz.common.mptree;




import jakarta.persistence.*;

/**
 *  @author Feroz Faiz
 * An abstract base entity for materialized path trees.
 * All tree-enabled models should extend this class.
 */
@MappedSuperclass
public abstract class MPNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The materialized path that represents this node's position in the tree.
     * The path is built from fixed-width segments (e.g., 4-character segments).
     * Example: "0001", "00010001", etc.
     */
    @Column(nullable = false, unique = true, length = 255)
    private String path;

    /**
     * The depth (level) of the node in the tree.
     * This is calculated as the number of segments in the path.
     */
    @Column(nullable = false)
    private int depth;

    /**
     * The number of direct child nodes.
     */
    @Column(nullable = false)
    private int numChild;

    /**
     * An example additional field.
     */
    @Column(length = 100)
    private String name;

    /**
     * An example additional field.
     */
    @Column(length = 255)
    private String description;

    // Constructors
    public MPNode() {
        // Default constructor
    }

    public MPNode(String path, int depth, int numChild, String name, String description) {
        this.path = path;
        this.depth = depth;
        this.numChild = numChild;
        this.name = name;
        this.description = description;
    }

    // Getters and setters
    public Long getId() {
        return id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}



