package com.ferozfaiz.product.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Feroz Faiz
 */
public interface ProductRepositoryCustom {

//    Page<Product> findAllWithJoinsAndSort(Predicate predicate, Pageable pageable);
    Page<ProductDto> findAllByFilter(ProductFilter filter, Pageable pageable);
}