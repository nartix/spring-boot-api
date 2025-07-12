package com.ferozfaiz.cti_product.measurementunit;

import jakarta.persistence.*;

@Entity
@Table(name = "cti_measurementunit", indexes = {
        @Index(name = "idx_measurementunit_name", columnList = "name"),
        @Index(name = "idx_measurementunit_symbol", columnList = "symbol")
})
public class MeasurementUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "symbol", nullable = false, length = 10)
    private String symbol;

    @Column(name = "conversion_factor")
    private Float conversionFactor;

    public MeasurementUnit() {}

    public MeasurementUnit(String name, String symbol, Float conversionFactor) {
        this.name = name;
        this.symbol = symbol;
        this.conversionFactor = conversionFactor;
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Float getConversionFactor() {
        return conversionFactor;
    }

    public void setConversionFactor(Float conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    @Override
    public String toString() {
        return name;
    }
}

