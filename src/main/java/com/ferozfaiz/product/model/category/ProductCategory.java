package com.ferozfaiz.product.model.category;

import com.ferozfaiz.common.tree.materializedpath.MaterializedPathNode;
import jakarta.persistence.*;

/**
 * @author Feroz Faiz
 */
@Entity
@Table(name = "product_category", indexes = {
        @Index(columnList = "name"),
        @Index(columnList = "path")
})
public class ProductCategory extends MaterializedPathNode<ProductCategory> {

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

    public ProductCategory() {
    }

    public ProductCategory(String path, int depth, int numChild, String name) {
        super(path, depth, numChild, name);
        this.name = name;
    }

    public ProductCategory(String name) {
        this.name = name;
    }
}
