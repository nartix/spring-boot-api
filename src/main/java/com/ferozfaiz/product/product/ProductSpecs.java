package com.ferozfaiz.product.product;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Map;

/**
 * @author Feroz Faiz
 */
public final class ProductSpecs {

    private ProductSpecs() {}

    /**
     * Eager‐fetch all associations once, and apply DISTINCT to avoid duplicates.
     */
    public static Specification<Product> withAllAssociations() {
        return (root, query, cb) -> {
            // only fetch for the content query, not the count
            if (Product.class.equals(query.getResultType())) {
                root.fetch("brand", JoinType.LEFT);
                root.fetch("manufacturer", JoinType.LEFT);
                root.fetch("currentPriceHistory", JoinType.LEFT);

                // productAttributes → attributeValueNumeric → attribute
                Fetch<Product, ?> paFetch = root.fetch("productAttributes", JoinType.LEFT);
                Fetch<?, ?> avFetch = paFetch.fetch("attributeValueNumeric", JoinType.LEFT);
                avFetch.fetch("attribute", JoinType.LEFT);

                query.distinct(true);
            }
            return null;
        };
    }

    /**
     * Build a predicate for each request‐param (minus page/size/sort).
     */
    public static Specification<Product> byRequestParams(Map<String, String[]> params) {
        Specification<Product> spec = Specification.where(null);

        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String key = entry.getKey();
            // skip paging and sorting params
            if (key.equalsIgnoreCase("page")
                    || key.equalsIgnoreCase("size")
                    || key.equalsIgnoreCase("sort")) {
                continue;
            }

            String[] values = entry.getValue();
            spec = spec.and((root, query, cb) -> {
                Path<?> path = buildPath(root, key);
                // for simplicity, we do an IN(...) filter; adapt to equals, like, etc.
                return path.in(Arrays.asList(values));
            });
        }

        return spec;
    }

    /**
     * Utility to turn "nested.prop.path" into a LEFT JOIN path.
     */
    @SuppressWarnings("unchecked")
    private static <Y> Path<Y> buildPath(Root<Product> root, String propertyPath) {
        String[] parts = propertyPath.split("\\.");
        Path<?> path = root;
        for (String part : parts) {
            path = ((From<?, ?>) path).join(part, JoinType.LEFT);
        }
        return (Path<Y>) path;
    }

    /**
     * Combine everything.
     */
    public static Specification<Product> build(Map<String, String[]> params) {
        return Specification
                .where(withAllAssociations())
                .and(byRequestParams(params));
    }
}
