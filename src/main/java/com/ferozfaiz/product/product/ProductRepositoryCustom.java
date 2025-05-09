package com.ferozfaiz.product.product;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author Feroz Faiz
 */
public interface ProductRepositoryCustom {

//    Page<Product> findAllByAttributeAndSort(
//            String attributeName,
//            List<String> sortBy,
//            List<Sort.Direction> directions,
//            Pageable pageable
//    );

    Page<Product> findAllWithJoinsAndSort(Predicate predicate, Pageable pageable);
}