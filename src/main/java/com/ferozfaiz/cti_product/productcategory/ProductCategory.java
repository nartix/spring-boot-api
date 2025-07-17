package com.ferozfaiz.cti_product.productcategory;

import com.ferozfaiz.cti_product.category.Category;
import com.ferozfaiz.cti_product.product.Product;
import jakarta.persistence.*;

@Entity(name = "CTIProductCategory")
@Table(name = "cti_productcategory", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "category_id"})
}, indexes = {
        @Index(name = "cti_productcategory_product_category_idx", columnList = "product_id, category_id")
})
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category Category;

    public ProductCategory() {}

    public ProductCategory(Product product, Category Category) {
        this.product = product;
        this.Category = Category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Category getCategory() { return Category; }
    public void setCategory(Category Category) { this.Category = Category; }

    @Override
    public String toString() {
        return product.getName() + " - " + Category.getName();
    }
}

