package com.ferozfaiz.product.attributevalue;

import java.util.List;

/**
 * @author Feroz Faiz
 */
public interface ProductAttributeValueService {

    ProductAttributeValue create(ProductAttributeValue value);

    List<ProductAttributeValue> findAll();

    ProductAttributeValue findById(Integer id);

    List<ProductAttributeValue> findByAttributeId(Integer attributeId);

    List<ProductAttributeValue> findByMeasurementUnitId(Integer measurementUnitId);

    ProductAttributeValue update(Integer id, ProductAttributeValue value);

    void delete(Integer id);
}