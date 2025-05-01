package com.ferozfaiz.product.productcategory;

import com.ferozfaiz.product.category.ProductCategory;
import com.ferozfaiz.product.product.Product;
import jakarta.persistence.*;

/**
 * @author Feroz Faiz
 */
@Entity
@Table(
        name = "product_productcategory",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_product_productcategory_product_category",
                        columnNames = {"product_id", "category_id"}
                )
        },
        indexes = {
                @Index(
                        name = "product_productcategory_product_category_idx",
                        columnList = "product_id, category_id"
                )
        }
)
public class ProductProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // FK → product_product.id, ON DELETE CASCADE
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_product_productcategory_product",
                    foreignKeyDefinition =
                            "FOREIGN KEY (product_id) REFERENCES product_product(id) ON DELETE CASCADE"
            )
    )
    private Product product;

    // FK → product_category.id, ON DELETE CASCADE
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "category_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_product_productcategory_category",
                    foreignKeyDefinition =
                            "FOREIGN KEY (category_id) REFERENCES product_category(id) ON DELETE CASCADE"
            )
    )
    private ProductCategory category;

    public ProductProductCategory() {}

    public ProductProductCategory(Product product, ProductCategory category) {
        this.product = product;
        this.category = category;
    }

    // Getters & setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    // equals() & hashCode() on id, toString() if desired
}