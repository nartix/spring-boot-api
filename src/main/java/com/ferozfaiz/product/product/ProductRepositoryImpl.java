package com.ferozfaiz.product.product;

import com.ferozfaiz.product.attribute.QProductAttribute;
import com.ferozfaiz.product.attributevalue.QProductAttributeValue;
import com.ferozfaiz.product.brand.QProductBrand;
import com.ferozfaiz.product.measurementunit.QProductMeasurementUnit;
import com.ferozfaiz.product.productattribute.QProductProductAttribute;
import com.ferozfaiz.product.productpricehistory.QProductPriceHistory;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author Feroz Faiz
 */
@Repository
public class ProductRepositoryImpl
        extends QuerydslRepositorySupport
        implements ProductRepositoryCustom {
    private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryImpl.class);

    private final JPAQueryFactory qf;

    public ProductRepositoryImpl(EntityManager em) {
        super(Product.class);
        this.qf = new JPAQueryFactory(em);
    }
//
//    @Override
//    public Page<Product> findAllWithJoinsAndSort(Predicate predicate, Pageable pageable) {
//        QProduct p  = QProduct.product;
//        QProductBrand b    = QProductBrand.productBrand;
//        QProductPriceHistory cph = QProductPriceHistory.productPriceHistory;
//        QProductProductAttribute pa = QProductProductAttribute.productProductAttribute;
//        QProductAttributeValue av   = QProductAttributeValue.productAttributeValue;
//        QProductAttribute a         = QProductAttribute.productAttribute;
//        QProductMeasurementUnit mu  = QProductMeasurementUnit.productMeasurementUnit;
//
//        // 1) build the base query with all the fetch-joins
//        var query = qf.selectDistinct(p)
//                .from(p)
//                .leftJoin(p.brand, b).fetchJoin()
//                .leftJoin(p.currentPriceHistory, cph).fetchJoin()
//                .leftJoin(p.productAttributes, pa).fetchJoin()
//                .leftJoin(pa.attributeValue,    av).fetchJoin()
//                .leftJoin(av.attribute,          a).fetchJoin()
//                .leftJoin(av.measurementUnit,   mu).fetchJoin()
//                .where(predicate);
//
//        // 2) translate Pageable.sort → Querydsl OrderSpecifiers
////        List<OrderSpecifier<?>> orders = new ArrayList<>();
////        for (Sort.Order order : pageable.getSort()) {
////            String prop = order.getProperty();
////            boolean asc = order.isAscending();
////            switch (prop) {
////                case "name":
////                    orders.add( asc ? p.name.asc() : p.name.desc() );
////                    break;
////                case "price":
////                    orders.add( asc ? cph.price.asc() : cph.price.desc() );
////                    break;
////                case "productAttributes.attributeValue.valueNumeric":
////                    orders.add( asc ? av.valueNumeric.asc() : av.valueNumeric.desc() );
////                    break;
////                // add more cases if you need to sort by other nested fields
////                default:
////                    // ignore or throw
////            }
////        }
//// in src/main/java/com/ferozfaiz/product/product/ProductRepositoryImpl.java
//
//        List<OrderSpecifier<?>> orders = new ArrayList<>();
//        PathBuilder<Product> root = new PathBuilder<>(Product.class, p.getMetadata());
//
//        for (Sort.Order order : pageable.getSort()) {
//            PathBuilder<Comparable> path = null;
//
//            // drill down each nested property as Comparable
//            for (String segment : order.getProperty().split("\\.")) {
//                if (path == null) {
//                    // first segment off of the root entity
//                    path = root.get(segment, Comparable.class);
//                } else {
//                    path = path.get(segment, Comparable.class);
//                }
//            }
//
//            if (path != null) {
//                orders.add(new OrderSpecifier<>(
//                        order.isAscending() ? Order.ASC : Order.DESC,
//                        path  // PathBuilder<Comparable> implements Expression<Comparable>
//                ));
//            }
//        }
//
//        if (!orders.isEmpty()) {
//            query.orderBy(orders.toArray(new OrderSpecifier[0]));
//        }
//
//        // log the list and each element
//        logger.info("Order specifiers (total={}): {}", orders.size(), orders);
//        orders.forEach(o -> logger.debug("  -> {}", o));
//        if (!orders.isEmpty()) {
//            query.orderBy(orders.toArray(new OrderSpecifier[0]));
//        }
//
//        // 3) apply pagination (this runs COUNT + SELECT)
//        var results = getQuerydsl().applyPagination(pageable, query).fetchResults();
//        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
//    }

    @Override
    public Page<Product> findAllWithJoinsAndSort(Predicate predicate, Pageable pageable) {
        QProduct p   = QProduct.product;
        QProductBrand b = QProductBrand.productBrand;
        QProductPriceHistory cph = QProductPriceHistory.productPriceHistory;
        QProductProductAttribute pa = QProductProductAttribute.productProductAttribute;
        QProductAttributeValue av   = QProductAttributeValue.productAttributeValue;
        QProductAttribute a         = QProductAttribute.productAttribute;
        QProductMeasurementUnit mu  = QProductMeasurementUnit.productMeasurementUnit;

        // 1) Total count (same joins as predicate, no fetch joins)
        long total = qf.select(p.id.countDistinct())
                .from(p)
                .leftJoin(p.productAttributes, pa)
                .leftJoin(pa.attributeValue,    av)
                .leftJoin(av.attribute,          a)
                .where(predicate)
                .fetchOne();

        List<OrderSpecifier<?>> orders = pageable.getSort().stream()
                .map(order -> {
                    boolean asc = order.isAscending();
                    String prop = order.getProperty();
                    switch (prop) {
                        case "productAttributes.attributeValue.valueNumeric":
                            return asc
                                    ? av.valueNumeric.asc()
                                    : av.valueNumeric.desc();
                        // other cases...
                        default:
                            return asc
                                    ? p.name.asc()
                                    : p.name.desc();
                    }
                })
                .collect(toList());

        List<Product> content = qf.select(p).distinct()
                .from(p)
                .leftJoin(p.brand, b)                     // still join to-one eagerly
                .leftJoin(p.currentPriceHistory, cph)
                .leftJoin(p.productAttributes, pa)        // normal join
                .leftJoin(pa.attributeValue,    av)
                .leftJoin(av.attribute,          a)
                .leftJoin(av.measurementUnit,   mu)
                .where(predicate)
                .orderBy(orders.toArray(new OrderSpecifier[0]))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }


}