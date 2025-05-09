package com.ferozfaiz.product.product;

import com.ferozfaiz.product.productattribute.ProductProductAttributeProjection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Feroz Faiz
 */
@Projection(name = "ProductProjection", types = Product.class)
public interface ProductProjection {
    Integer getId();

    String getName();

    String getDescription();

    String getSlug();

//    ProductBrand getBrand();

//    ProductManufacturer getManufacturer();

    Boolean getIsActive();

    Integer getViewCount();


    //    @Value("#{target.currentPriceHistory?.get(0)?.price}")
//    @Value("#{target.priceHistories?.get(0)?.price}")
    @Value("#{target.currentPriceHistory?.price}")
    BigDecimal getPrice();
//
    List<ProductProductAttributeProjection> getProductAttributes();
}
