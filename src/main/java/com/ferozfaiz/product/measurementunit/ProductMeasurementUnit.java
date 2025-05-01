package com.ferozfaiz.product.measurementunit;

import jakarta.persistence.*;

/**
 * @author Feroz Faiz
 */

@Entity
@Table(
        name = "product_measurementunit",
        indexes = {
                @Index(name = "product_measurementunit_name_idx", columnList = "name"),
                @Index(name = "product_measurementunit_symbol_idx", columnList = "symbol")
        }
)
public class ProductMeasurementUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 10)
    private String symbol;

    @Column(name = "conversion_factor")
    private Double conversionFactor;

    public ProductMeasurementUnit() {}

    public ProductMeasurementUnit(String name, String symbol, Double conversionFactor) {
        this.name = name;
        this.symbol = symbol;
        this.conversionFactor = conversionFactor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getConversionFactor() {
        return conversionFactor;
    }

    public void setConversionFactor(Double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    // equals()/hashCode() on id, and toString() for logging if desired
}
