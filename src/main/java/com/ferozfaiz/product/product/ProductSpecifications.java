package com.ferozfaiz.product.product;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author Feroz Faiz
 */
public class ProductSpecifications {
    /**
     * Returns a Specification that matches Products whose name contains
     * the given expression (case-insensitive).
     */
    public static Specification<Product> nameContains(String expression) {
        return (root, query, cb) -> {
            if (expression == null || expression.isBlank()) {
                // no filtering
                return cb.conjunction();
            }
            // lower(name) LIKE %lower(expression)%
            return cb.like(
                    cb.lower(root.get("name")),
                    "%" + expression.toLowerCase() + "%"
            );
        };
    }
}