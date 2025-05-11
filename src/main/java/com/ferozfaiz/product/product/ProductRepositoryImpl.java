package com.ferozfaiz.product.product;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author Feroz
 */
@Repository
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<ProductDto> findAllByFilter(ProductFilter filter, Pageable pageable) {
        // ----------------------------
        // 1) Build the FROM + JOIN clauses
        // ----------------------------
        String joins = ""
                + "FROM Product p "
                + " LEFT JOIN p.brand b "
                + " LEFT JOIN p.manufacturer m "
                + " LEFT JOIN p.currentPriceHistory cph "
                // always join attributes for DTO (we need every attribute row)
                + " JOIN p.productAttributes pa "
                + " JOIN pa.attributeValue av "
                + " JOIN av.attribute a "
                + " LEFT JOIN av.measurementUnit mu ";

        // ----------------------------
        // 2) Build WHERE dynamically (exactly as before)
        // ----------------------------
        StringBuilder where = new StringBuilder("WHERE 1=1 ");
        Map<String, Object> params = new HashMap<>();

        if (filter.getName() != null) {
            where.append("AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) ");
            params.put("name", filter.getName());
        }
        if (filter.getMinBasePrice() != null) {
            where.append("AND p.basePrice >= :minBasePrice ");
            params.put("minBasePrice", filter.getMinBasePrice());
        }
        if (filter.getMaxBasePrice() != null) {
            where.append("AND p.basePrice <= :maxBasePrice ");
            params.put("maxBasePrice", filter.getMaxBasePrice());
        }
        if (filter.getActive() != null) {
            where.append("AND p.isActive = :active ");
            params.put("active", filter.getActive());
        }
        if (filter.getBrandName() != null) {
            where.append("AND LOWER(b.name) = LOWER(:brandName) ");
            params.put("brandName", filter.getBrandName());
        }
        if (filter.getManufacturerName() != null) {
            where.append("AND LOWER(m.name) = LOWER(:manufacturerName) ");
            params.put("manufacturerName", filter.getManufacturerName());
        }
        if (filter.getAttributeName() != null) {
            where.append("AND LOWER(a.name) = LOWER(:attributeName) ");
            params.put("attributeName", filter.getAttributeName());
        }
        if (filter.getAttributeValueNumeric() != null) {
            where.append("AND av.valueNumeric = :attributeValueNumeric ");
            params.put("attributeValueNumeric", filter.getAttributeValueNumeric());
        }
        // … add more filters as needed …

        // ----------------------------
        // 3) COUNT query (same as before)
        // ----------------------------
        String countJpql = "SELECT COUNT(DISTINCT p.id) " + joins + where;
        TypedQuery<Long> countQ = em.createQuery(countJpql, Long.class);
        params.forEach(countQ::setParameter);
        long total = countQ.getSingleResult();

        // ----------------------------
        // 4) DATA query: select flat rows for DTO-construction
        // ----------------------------
        String selectJpql = ""
                + "SELECT p.id AS id, "
                + "       p.name AS name, "
                + "       a.name AS attributeName, "
                + "       av.valueNumeric AS attributeValue, "
                + "       mu.symbol AS measurementUnitSymbol "
                + joins
                + where
                + buildOrderBy(pageable.getSort());

        TypedQuery<Tuple> dataQ = em.createQuery(selectJpql, Tuple.class);
        params.forEach(dataQ::setParameter);
        dataQ.setFirstResult((int) pageable.getOffset());
        dataQ.setMaxResults(pageable.getPageSize());

        List<Tuple> tuples = dataQ.getResultList();

        LinkedHashMap<Long, ProductDtoBuilder> productMap = new LinkedHashMap<>();
        for (Tuple tuple : tuples) {
            Long productId = tuple.get("id", Long.class);
            String productName = tuple.get("name", String.class);
            String attributeName = tuple.get("attributeName", String.class);
            Double attributeValue = tuple.get("attributeValue", Number.class) != null ?
                    tuple.get("attributeValue", Number.class).doubleValue() : null;
            String measurementUnitSymbol = tuple.get("measurementUnitSymbol", String.class);

            productMap
                    .computeIfAbsent(productId, id -> new ProductDtoBuilder(productId, productName))
                    .addAttribute(new AttributeDto(attributeName, attributeValue, measurementUnitSymbol));
        }

        List<ProductDto> productDtos = productMap.values()
                .stream()
                .map(ProductDtoBuilder::build)
                .toList();

        return new PageImpl<>(productDtos, pageable, total);
    }

    // ----------------------------------------------------------------
    // unchanged: dynamic ORDER BY builder
    // ----------------------------------------------------------------
    private String buildOrderBy(Sort sort) {
        if (!sort.isSorted()) {
            return "";
        }
        List<String> clauses = new ArrayList<>();
        for (Sort.Order o : sort) {
            String aliasPath = switch (o.getProperty()) {
                case "name"                                           -> "p.name";
                case "basePrice"                                      -> "p.basePrice";
                case "brand.name"                                     -> "b.name";
                case "manufacturer.name"                              -> "m.name";
                case "currentPriceHistory.price", "currentPrice"      -> "cph.price";
                case "productAttributes.attributeValue.valueNumeric"  -> "av.valueNumeric";
                case "productAttributes.attributeValue.attribute.name"-> "a.name";
                default -> throw new IllegalArgumentException("Unknown sort property: " + o.getProperty());
            };
            clauses.add(aliasPath + " " + o.getDirection().name());
        }
        return " ORDER BY " + String.join(", ", clauses) + " ";
    }

    // ----------------------------------------------------------------
    // Builder classes for DTO grouping
    // ----------------------------------------------------------------
    private static class ProductDtoBuilder {
        private final Long id;
        private final String name;
        private final List<AttributeDto> attrs = new ArrayList<>();

        ProductDtoBuilder(Long id, String name) {
            this.id   = id;
            this.name = name;
        }

        void addAttribute(AttributeDto dto) {
            attrs.add(dto);
        }

        ProductDto build() {
            return new ProductDto(id, name, attrs);
        }
    }
}
