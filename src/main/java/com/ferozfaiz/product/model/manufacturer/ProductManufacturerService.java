package com.ferozfaiz.product.model.manufacturer;

import java.util.List;

/**
 * @author Feroz Faiz
 */
public interface ProductManufacturerService {

    ProductManufacturer create(ProductManufacturer manufacturer);

    List<ProductManufacturer> findAll();

    ProductManufacturer findById(Integer id);

    ProductManufacturer update(Integer id, ProductManufacturer manufacturer);

    void delete(Integer id);
}
