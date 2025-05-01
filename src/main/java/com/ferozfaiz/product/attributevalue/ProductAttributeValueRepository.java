package com.ferozfaiz.product.attributevalue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Feroz Faiz
 */
public interface ProductAttributeValueRepository
        extends JpaRepository<ProductAttributeValue, Integer> {

    /**
     * Lookup all values for a given attribute
     */
    List<ProductAttributeValue> findByAttributeId(Integer attributeId);

    /**
     * Lookup all values using a specific measurement unit
     */
    List<ProductAttributeValue> findByMeasurementUnitId(Integer measurementUnitId);
}