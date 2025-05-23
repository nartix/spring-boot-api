package com.ferozfaiz.product.product;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ferozfaiz.product.brand.ProductBrand;
import com.ferozfaiz.product.manufacturer.ProductManufacturer;
import com.ferozfaiz.product.productattribute.ProductProductAttribute;
import com.ferozfaiz.product.productpricehistory.ProductPriceHistory;
import jakarta.persistence.*;
import org.springframework.data.rest.core.annotation.RestResource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Feroz Faiz
 */

@Entity
@Table(
        name = "product_product",
        indexes = {
                @Index(name = "product_product_name_idx", columnList = "name"),
                @Index(name = "product_product_slug_idx", columnList = "slug"),
                @Index(name = "product_product_base_price_idx", columnList = "base_price"),
                @Index(name = "product_product_is_active_idx", columnList = "is_active")
        }
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(length = 255)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "brand_id",
            foreignKey = @ForeignKey(
                    name = "fk_product_product_brand",
                    foreignKeyDefinition =
                            "FOREIGN KEY (brand_id) REFERENCES product_brand(id) ON DELETE SET NULL"
            )
    )
    @RestResource(exported = false)
    private ProductBrand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "manufacturer_id",
            foreignKey = @ForeignKey(
                    name = "fk_product_product_manufacturer",
                    foreignKeyDefinition =
                            "FOREIGN KEY (manufacturer_id) REFERENCES product_manufacturer(id) ON DELETE SET NULL"
            )
    )
    @RestResource(exported = false)
    private ProductManufacturer manufacturer;

//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumnsOrFormulas({
//            // 1) map this entity's PK (p.id) to the FK column (h.product_id)
//            @JoinColumnOrFormula(
//                    column = @JoinColumn(
//                            name = "id",
//                            referencedColumnName = "product_id",
//                            insertable = false,   // if you don't want to write through
//                            updatable = false
//                    )
//            ),
            // 2) restrict to the row where is_current = true
            // else there will be duplicate rows
//            @JoinColumnOrFormula(
//                    formula = @JoinFormula(
//                            value = "true",
//                            referencedColumnName = "is_current"
//                    )
//            ),
//            @JoinColumnOrFormula(
//                    formula = @JoinFormula(
//                            value = "is_current = true OR end_date IS NULL OR end_date > NOW()",
//                            referencedColumnName = "true"
//                    )
//            )
//    })
    @RestResource(exported = false)
//    @JsonManagedReference()
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "product")
//    @SQLRestriction("is_current = true OR end_date IS NULL OR end_date > NOW()")
//    @SQLJoinTableRestriction("is_current = true OR end_date IS NULL OR end_date > NOW()")
    private ProductPriceHistory currentPriceHistory;

    public ProductPriceHistory getCurrentPriceHistory() {
        return currentPriceHistory;
    }

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @RestResource(exported = false)
    @JsonManagedReference
    private Set<ProductProductAttribute> productAttributes = new HashSet<>();

    public Set<ProductProductAttribute> getProductAttributes() {
        return productAttributes;
    }

    public void setProductAttributes(Set<ProductProductAttribute> productAttributes) {
        this.productAttributes = productAttributes;
    }

    public Product() {
    }

    public Product(String name) {
        this.name = name;
    }

    public Product(String name,
                   BigDecimal basePrice,
                   Boolean isActive,
                   ProductBrand brand,
                   ProductManufacturer manufacturer) {
        this.name = name;
        this.basePrice = basePrice;
        this.isActive = isActive;
        this.brand = brand;
        this.manufacturer = manufacturer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public ProductBrand getBrand() {
        return brand;
    }

    public void setBrand(ProductBrand brand) {
        this.brand = brand;
    }

    public ProductManufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(ProductManufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    // equals()/hashCode() on id, toString() if desired
}

