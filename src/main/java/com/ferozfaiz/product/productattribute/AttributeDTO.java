package com.ferozfaiz.product.productattribute;

/**
 * @author Feroz Faiz
 */

public class AttributeDTO {
    private Integer id;
    private String attributeName;
    private String value;   // or BigDecimal / Boolean, depending on the attribute type

    public AttributeDTO(Integer id, String attributeName, String value) {
        this.id = id;
        this.attributeName = attributeName;
        this.value = value;
    }

    public Integer getId() { return id; }
    public String getAttributeName() { return attributeName; }
    public String getValue() { return value; }
}