package com.ferozfaiz.product.product;

import com.ferozfaiz.product.brand.ProductBrand;
import com.ferozfaiz.product.manufacturer.ProductManufacturer;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * @author Feroz Faiz
 */

@Entity
@Table(
        name = "product_product",
        indexes = {
                @Index(name = "product_product_name_idx",     columnList = "name"),
                @Index(name = "product_product_slug_idx",     columnList = "slug"),
                @Index(name = "product_product_base_price_idx", columnList = "base_price"),
                @Index(name = "product_product_is_active_idx", columnList = "is_active")
        }
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "brand_id",
            foreignKey = @ForeignKey(
                    name = "fk_product_product_brand",
                    foreignKeyDefinition =
                            "FOREIGN KEY (brand_id) REFERENCES product_brand(id) ON DELETE SET NULL"
            )
    )
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
    private ProductManufacturer manufacturer;

    public Product() {}

    // Constructors, getters & setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

