package com.ferozfaiz.product.attribute;

import java.util.List;

/**
 * @author Feroz Faiz
 */
public interface ProductAttributeService {

    ProductAttribute create(ProductAttribute attribute);

    List<ProductAttribute> findAll();

    ProductAttribute findById(Integer id);

    ProductAttribute update(Integer id, ProductAttribute attribute);

    void delete(Integer id);
}
