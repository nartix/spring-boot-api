package com.ferozfaiz.product.measurementunit;

import com.ferozfaiz.product.brand.ProductBrand;

import java.util.List;

/**
 * @author Feroz Faiz
 */
public interface ProductBrandService {

    ProductBrand create(ProductBrand brand);

    List<ProductBrand> findAll();

    ProductBrand findById(Integer id);

    ProductBrand update(Integer id, ProductBrand brand);

    void delete(Integer id);
}