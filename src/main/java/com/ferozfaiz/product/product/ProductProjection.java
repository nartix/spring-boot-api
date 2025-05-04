package com.ferozfaiz.product.product;

import com.ferozfaiz.product.brand.ProductBrand;
import com.ferozfaiz.product.manufacturer.ProductManufacturer;

import org.springframework.data.rest.core.config.Projection;

/**
 * @author Feroz Faiz
 */
@Projection(name = "ProductProjection", types = Product.class)
public interface ProductProjection {
    Integer getId();

    String getName();

    String getDescription();

    String getSlug();

    ProductBrand getBrand();

    ProductManufacturer getManufacturer();

    Boolean getIsActive();

    Integer getViewCount();

}
