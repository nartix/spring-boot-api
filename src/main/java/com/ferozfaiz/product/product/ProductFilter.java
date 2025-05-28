package com.ferozfaiz.product.product;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Feroz Faiz
 */
public class ProductFilter {
    // validate name
    @Size(max = 255)
    private String name;

    @Size(max = 5)
    private List<@Size(max = 50) String> brandName;

    @Size(max = 5)
    private List<@Size(max = 50) String> manufacturerName;


    @Size(max = 5)
    public List<@Size(max = 50) String> getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(List<String> manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public List<String> getBrandName() {
        return brandName;
    }

    public void setBrandName(List<String> brandName) {
        this.brandName = brandName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<AttributeCriteria> attributeCriteria = new ArrayList<>();

    public List<AttributeCriteria> getAttributeCriteria() {
        return attributeCriteria;
    }

    public void setAttributeCriteria(List<AttributeCriteria> attributeCriteria) {
        this.attributeCriteria = attributeCriteria;
    }

    public static class AttributeCriteria {
        @NotNull
        @Size(max = 255)
        private String name;

        @Size(max = 5)
        private List<@Size(max = 255) String> valueStrings;

        @Size(max = 5)
        private List<
                @DecimalMin(value = "0.0", inclusive = true)
                @DecimalMax(value = "1.7976931348623157E308", inclusive = true)
                        BigDecimal
                > valueNumerics;

        // getters & setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getValueStrings() {
            return valueStrings;
        }

        public void setValueStrings(List<String> valueStrings) {
            this.valueStrings = valueStrings;
        }

        public List<BigDecimal> getValueNumerics() {
            return valueNumerics;
        }

        public void setValueNumerics(List<BigDecimal> valueNumerics) {
            this.valueNumerics = valueNumerics;
        }
    }
}