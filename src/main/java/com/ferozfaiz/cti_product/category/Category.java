package com.ferozfaiz.cti_product.category;

import com.ferozfaiz.common.tree.materializedpath.MaterializedPathNode;
import jakarta.persistence.*;

@Entity
@Table(name = "cti_category", indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "path")
})
public class Category extends MaterializedPathNode<Category> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public Category() {}

    public Category(String path, int depth, int numChild, String name, String description) {
        super(path, depth, numChild, name);
        this.name = name;
        this.description = description;
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Category: " + name;
    }
}

