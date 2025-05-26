package com.ferozfaiz.product.attributevalue;

import com.ferozfaiz.product.attribute.ProductAttribute;
import com.ferozfaiz.product.measurementunit.ProductMeasurementUnit;
import jakarta.persistence.*;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * @author Feroz Faiz
 */

@Entity
@Table(
        name = "product_attributevalue",
        indexes = {
                @Index(name = "product_attributevalue_attribute_idx", columnList = "attribute_id"),
                @Index(name = "product_attributevalue_attribute_id_value_string_idx", columnList = "attribute_id,value_string"),
                @Index(name = "product_attributevalue_attribute_id_value_numeric_idx", columnList = "attribute_id,value_numeric")
        }
)
//@RepositoryRestResource( exported = false )
public class ProductAttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // FK → product_attribute.id, ON DELETE CASCADE handled at DB level
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "attribute_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_product_attributevalue_attribute",
                    foreignKeyDefinition = "FOREIGN KEY (attribute_id) REFERENCES product_attribute(id) ON DELETE CASCADE"
            )
    )
    @RestResource(exported = false)
    private ProductAttribute attribute;

    @Column(name = "value_string", length = 255)
    private String valueString;

    @Column(name = "value_numeric")
    private Double valueNumeric;

    @Column(name = "value_boolean")
    private Boolean valueBoolean;

    // FK → product_measurementunit.id, ON DELETE SET NULL handled at DB level
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "measurement_unit_id",
            foreignKey = @ForeignKey(
                    name = "fk_product_attributevalue_measurement_unit",
                    foreignKeyDefinition = "FOREIGN KEY (measurement_unit_id) REFERENCES product_measurementunit(id) ON DELETE SET NULL"
            )
    )
    @RestResource(exported = false)
    private ProductMeasurementUnit measurementUnit;

    public ProductAttributeValue() {
    }

    public ProductAttributeValue(
            ProductAttribute attribute,
            String valueString,
            Double valueNumeric,
            Boolean valueBoolean,
            ProductMeasurementUnit measurementUnit) {
        this.attribute = attribute;
        this.valueString = valueString;
        this.valueNumeric = valueNumeric;
        this.valueBoolean = valueBoolean;
        this.measurementUnit = measurementUnit;
    }

    public ProductAttributeValue(ProductAttribute color, Object o, String red, Object o1) {
    }

    // — Getters & setters —

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProductAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(ProductAttribute attribute) {
        this.attribute = attribute;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public Double getValueNumeric() {
        return valueNumeric;
    }

    public void setValueNumeric(Double valueNumeric) {
        this.valueNumeric = valueNumeric;
    }

    public Boolean getValueBoolean() {
        return valueBoolean;
    }

    public void setValueBoolean(Boolean valueBoolean) {
        this.valueBoolean = valueBoolean;
    }

    public ProductMeasurementUnit getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(ProductMeasurementUnit measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    // equals()/hashCode() on id, toString() if desired
}
