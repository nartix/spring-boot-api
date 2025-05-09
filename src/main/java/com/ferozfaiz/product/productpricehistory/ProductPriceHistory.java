package com.ferozfaiz.product.productpricehistory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ferozfaiz.product.product.Product;
import jakarta.persistence.*;
import org.springframework.data.rest.core.annotation.RestResource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Feroz Faiz
 */
@Entity
//@FilterDef(name = "currentPriceFilter", defaultCondition = "is_current = true")
@Table(
        name = "product_productpricehistory",
        indexes = {
                // JPA doesn’t standardize index directions; DB will use descending on start_date as defined in Liquibase
                @Index(
                        name = "product_productpricehistory_product_start_date_idx",
                        columnList = "product_id, start_date"
                ),
                @Index(
                        name = "product_productpricehistory_is_current_idx",
                        columnList = "is_current"
                )
        }
)
public class ProductPriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // FK → product_product.id, ON DELETE CASCADE
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_product_productpricehistory_product",
                    foreignKeyDefinition =
                            "FOREIGN KEY (product_id) REFERENCES product_product(id) ON DELETE CASCADE"
            )
    )
    @JsonBackReference
    @RestResource(exported = false)
    private Product product;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent = true;

    public ProductPriceHistory() {}

    public ProductPriceHistory(
            Product product,
            BigDecimal price,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Boolean isCurrent) {
        this.product    = product;
        this.price      = price;
        this.startDate  = startDate;
        this.endDate    = endDate;
        this.isCurrent  = isCurrent;
    }

    // — Getters & Setters —

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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean current) {
        isCurrent = current;
    }

    // equals()/hashCode() on id, toString() if desired
}