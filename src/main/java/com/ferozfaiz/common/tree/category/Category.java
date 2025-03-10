package com.ferozfaiz.common.tree.category;

import com.ferozfaiz.common.tree.materializedpath.MaterializedPathNode;
import jakarta.persistence.*;

/**
 * @author Feroz Faiz
 */
@Entity
@Table(name = "category")
public class Category extends MaterializedPathNode<Category> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category() {
    }

    public Category(String path, int depth, int numChild, String name) {
        super(path, depth, numChild, name);
        this.name = name;
    }

    public Category(String name) {
        this.name = name;
    }

    public static int getPathColumnLength() {
        return 300;
    }
}