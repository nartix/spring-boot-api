package com.ferozfaiz.product.product;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Feroz Faiz
 */
public class ProductFilter {
    private String name;
    private BigDecimal minBasePrice;
    private BigDecimal maxBasePrice;
    private Boolean active;
    private String brandName;
    private String manufacturerName;
    private String attributeName;
    private List<BigDecimal> attributeValueNumeric;
    private List<String> attributeValueString;

    public List<BigDecimal> getAttributeValueNumeric() {
        return attributeValueNumeric;
    }

    public void setAttributeValueNumeric(List<BigDecimal> attributeValueNumeric) {
        this.attributeValueNumeric = attributeValueNumeric;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public BigDecimal getMaxBasePrice() {
        return maxBasePrice;
    }

    public void setMaxBasePrice(BigDecimal maxBasePrice) {
        this.maxBasePrice = maxBasePrice;
    }

    public BigDecimal getMinBasePrice() {
        return minBasePrice;
    }

    public void setMinBasePrice(BigDecimal minBasePrice) {
        this.minBasePrice = minBasePrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAttributeValueString() {
        return attributeValueString;
    }

    public void setAttributeValueString(List<String> attributeValueString) {
        this.attributeValueString = attributeValueString;
    }
}