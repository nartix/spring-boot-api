package com.ferozfaiz.product.productattribute;

import java.util.List;

/**
 * @author Feroz Faiz
 */

public interface ProductProductAttributeService {

    ProductProductAttribute create(ProductProductAttribute ppa);

    List<ProductProductAttribute> findAll();

    ProductProductAttribute findById(Integer id);

    List<ProductProductAttribute> findByProductId(Integer productId);

    List<ProductProductAttribute> findByAttributeValueId(Integer attributeValueId);

    void delete(Integer id);
}