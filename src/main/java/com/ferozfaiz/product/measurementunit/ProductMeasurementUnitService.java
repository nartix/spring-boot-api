package com.ferozfaiz.product.measurementunit;

import java.util.List;

/**
 * @author Feroz Faiz
 */
public interface ProductMeasurementUnitService {

    ProductMeasurementUnit create(ProductMeasurementUnit unit);

    List<ProductMeasurementUnit> findAll();

    ProductMeasurementUnit findById(Integer id);

    ProductMeasurementUnit update(Integer id, ProductMeasurementUnit unit);

    void delete(Integer id);
}