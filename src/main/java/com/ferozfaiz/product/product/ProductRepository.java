package com.ferozfaiz.product.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Feroz Faiz
 * Cannot do complex queries with deeply nested sets with Spring Data REST
 * with sort, filter, and pagination all on a single api endpoint.
 * QueryDSL and JPA Specification/Criteria API run into issues with the
 * firstResult/maxResults where it loads all the data
 * into memory before pagination is applied and then
 * skips sorting and filtering on the query.
 * Also, deeply nested ordering is not supported and ignored by Spring Data REST.
 * I created a custom API to handle this.
 */
@RepositoryRestResource(collectionResourceRel = "products", path = "products", excerptProjection = ProductProjection.class)
public interface ProductRepository extends JpaRepository<Product, Integer>,JpaSpecificationExecutor<Product>, ProductRepositoryCustom  {
// public interface ProductRepository extends JpaRepository<Product, Integer>, QuerydslPredicateExecutor<Product>, QuerydslBinderCustomizer<QProduct>, ProductRepositoryCustom, JpaSpecificationExecutor<Product> {
//public interface ProductRepository extends JpaRepository<Product, Integer>, QuerydslPredicateExecutor<Product>,
//        QuerydslBinderCustomizer<QProduct> {

    Optional<Product> findBySlug(String slug);

    List<Product> findByNameContaining(String nameFragment);

    List<Product> findByIsActiveTrue();

    List<Product> findByBasePriceBetween(BigDecimal min, BigDecimal max);

    // This method is used to fetch all products with their brand information
    // using join fetch otherwise it'll create separate query to fetch brand
    // which will cause N+1 problem
    @Override
    @EntityGraph(attributePaths = {
//            "brand",
            "currentPriceHistory",
            "productAttributes",
            "productAttributes.attributeValue",                           // nested
            "productAttributes.attributeValue.attribute",                  // deeper nested
            "productAttributes.attributeValue.measurementUnit"             // measurement unit
    })
    Page<Product> findAll(Pageable pageable);
    @Override
    @EntityGraph(attributePaths = {
//            "brand",
//            "manufacturer",
            "currentPriceHistory",
            "productAttributes",
            "productAttributes.attributeValue",                           // nested
            "productAttributes.attributeValue.attribute",                  // deeper nested
            "productAttributes.attributeValue.measurementUnit"             // measurement unit
    })
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

//    @Override
//    default void customize(QuerydslBindings bindings, QProduct root) {
//        // bind nested attribute name
//        bindings.bind(root.productAttributes
//                        .any()
//                        .attributeValue
//                        .attribute
//                        .name)
//                .first((path, value) -> path.eq(value));
//
//        // bind nested numeric value
//        bindings.bind(root.productAttributes
//                        .any()
//                        .attributeValue
//                        .valueNumeric)
//                .first((path, value) -> path.eq(value));
//    }

    /**
     * THIS default method *replaces* the standard QuerydslPredicateExecutor.findAll(...)
     * so that Spring Data REST’s filtering + sorting all end up calling our
     * join-fetch + dynamic sort implementation above.
     */
//    @Override
//    default Page<Product> findAll(Predicate predicate, Pageable pageable) {
//        return findAllWithJoinsAndSort(predicate, pageable);
//    }


    /**
     * Bind the ‘attributeName’ filter so that
     * ?productAttributes.attributeValue.attribute.name=Width
     * becomes a Predicate on `a.name.eq("Width")`.
     */
//    @Override
//    default void customize(QuerydslBindings bindings, QProduct root) {
//        bindings.bind(root.productAttributes.any().attributeValue.attribute.name).first((path, value) -> path.eq(value));
//    }
}
