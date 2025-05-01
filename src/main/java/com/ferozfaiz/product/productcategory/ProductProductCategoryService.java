package com.ferozfaiz.product.productcategory;

import java.util.List;

/**
 * @author Feroz Faiz
 */
public interface ProductProductCategoryService {

    ProductProductCategory create(ProductProductCategory ppc);

    List<ProductProductCategory> findAll();

    ProductProductCategory findById(Integer id);

    List<ProductProductCategory> findByProductId(Integer productId);

    List<ProductProductCategory> findByCategoryId(Integer categoryId);

    void delete(Integer id);
}