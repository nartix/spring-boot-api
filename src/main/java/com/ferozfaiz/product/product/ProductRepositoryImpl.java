package com.ferozfaiz.product.product;


import com.ferozfaiz.product.attribute.AttributeDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// http://localhost:8080/api/products?sort=valueNumeric,desc&attributeName=Width&attributeValueNumeric=55&&attributeValueNumeric=5
// http://localhost:8080/api/products?sort=valueNumeric,asc&attributeName=Dimension&sort=name,asc

/**
 * @author Feroz
 */
@Repository
@Primary
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    // i want to access environment variable class
    private final Environment env;

    public ProductRepositoryImpl(Environment env) {
        this.env = env;
    }

    @Override
    public Page<ProductDto> findAllByFilter(ProductFilter filter, Pageable pageable) {

        // Limit the page size to a maximum of 1000
        if (pageable.getPageSize() >= 1000 ) {
            throw new IllegalArgumentException("Page size must be less than 1000");
        }

        // 1) Static joins for brand/manufacturer/price
        String baseJoins = ""
                + " FROM Product p"
                + " LEFT JOIN p.brand b"
                + " LEFT JOIN p.manufacturer m"
                + " LEFT JOIN p.currentPriceHistory cph"
                + "   WITH cph.isCurrent = true";
//                + "   WITH (cph.isCurrent = true OR cph.endDate IS NULL OR cph.endDate >= CURRENT_TIMESTAMP )";

        // 2) Dynamic WHERE (note: we no longer filter out products missing the attribute)
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        Map<String, Object> params = new HashMap<>();
        if (filter.getName() != null) {
            where.append(" AND LOWER(p.name) LIKE LOWER(CONCAT('%',:name,'%'))");
            params.put("name", filter.getName());
        }

        if (filter.getBrandName() != null && !filter.getBrandName().isEmpty()) {
            List<String> lowered = filter.getBrandName().stream()
                    .map(String::toLowerCase)
                    .toList();
            where.append(" AND LOWER(b.name) IN :brandNames");
            params.put("brandNames", lowered);
        }
        if (filter.getManufacturerName() != null && !filter.getManufacturerName().isEmpty()) {
            List<String> lowered = filter.getManufacturerName().stream()
                    .map(String::toLowerCase)
                    .toList();
            where.append(" AND LOWER(m.name) IN :manufacturerNames");
            params.put("manufacturerNames", lowered);
        }
        // … any other filters, but *no* attributeName filter here …


        // Decided not go with inner join here, as it requires the use of DISTINCT
        // for all ORDER BY fields.
//        filterJoins = ""
//                + " JOIN p.productAttributes paF"
//                + "   WITH LOWER(paF.attributeValue.attribute.name) = LOWER(:attributeName)"
//                + "   AND LOWER(paF.attributeValue.valueString) IN :valueStrings"
//                + " JOIN paF.attributeValue avF";
        // 2a) filter by attribute-valueStrings (e.g. Speed = {DDR4, DDR5})
// … inside findAllByFilter, after you’ve built `baseJoins`, `where`, and `params` …


        List<ProductFilter.AttributeCriteria> crits = filter.getAttributeCriteria();
        for (int i = 0; i < crits.size(); i++) {
            var ac = crits.get(i);
            String nameParam = "attrName" + i;
            where.append("""
                        AND EXISTS (
                          SELECT pa%s FROM p.productAttributes pa%s
                          JOIN pa%s.attributeValue av%s
                         WHERE LOWER(av%s.attribute.name) = LOWER(:%s)
                    """.formatted(i, i, i, i, i, nameParam));
            params.put(nameParam, ac.getName());

            if (ac.getValueStrings() != null && !ac.getValueStrings().isEmpty()) {
                String strParam = "stringVals" + i;
                where.append("    AND LOWER(av%s.valueString) IN :%s\n".formatted(i, strParam));
                params.put(strParam,
                        ac.getValueStrings().stream()
                                .map(String::toLowerCase)
                                .toList());
            }
            if (ac.getValueNumerics() != null && !ac.getValueNumerics().isEmpty()) {
                String numParam = "numVals" + i;
                where.append("    AND av%s.valueNumeric IN :%s\n".formatted(i, numParam));
                params.put(numParam, ac.getValueNumerics());
            }
            where.append(") ");
        }


        // 3) Total count on all matching products
        String countJpql = "SELECT COUNT(p.id)" + baseJoins + where;
        TypedQuery<Long> countQ = em.createQuery(countJpql, Long.class);
        params.forEach(countQ::setParameter);
        long total = countQ.getSingleResult();

        // 4) Build a LEFT JOIN … WITH … for sorting by the chosen attribute
//        String sortJoins = "";
//        if (filter.getAttributeName() != null) {
//            sortJoins = ""
//                    + " LEFT JOIN p.productAttributes paSort"
//                    + "   WITH LOWER(paSort.attributeValue.attribute.name) = LOWER(:attributeName)"
//                    + " LEFT JOIN paSort.attributeValue avSort";
//            params.put("attributeName", filter.getAttributeName());
//        }

        // 4) Build dynamic sort‐joins
        StringBuilder sortJoins = new StringBuilder();
        List<Sort.Order> orders = pageable.getSort().toList();

        for (int i = 0; i < orders.size(); i++) {
            Sort.Order o = orders.get(i);
            String prop = o.getProperty().toLowerCase(Locale.ROOT);

            // look for attributeCriteria[index].valueNumeric
            Matcher mNum = Pattern.compile("^attributecriteria\\[(\\d+)\\]\\.valuenumeric$")
                    .matcher(prop);
            Matcher mStr = Pattern.compile("^attributecriteria\\[(\\d+)\\]\\.valuestring$")
                    .matcher(prop);

            if (mNum.matches() || mStr.matches()) {
                int critIndex = Integer.parseInt(
                        mNum.matches() ? mNum.group(1) : mStr.group(1)
                );

                String paAlias = "paSort" + critIndex;
                String avAlias = "avSort" + critIndex;

// 1) join on the right criterion name
                sortJoins.append("""
                        LEFT JOIN p.productAttributes %s
                          WITH LOWER(%s.attributeValue.attribute.name) = LOWER(:attrName%s)
                        """.formatted(paAlias, paAlias, critIndex));

                // 2) then join its value
                sortJoins.append(" LEFT JOIN " + paAlias + ".attributeValue " + avAlias + "\n");

                // we’ll bind :attrName{critIndex} from your filter attributeCriteria list
                params.put("attrName" + critIndex,
                        filter.getAttributeCriteria()
                                .get(critIndex)
                                .getName());
            }
        }

        // 5) ID query: apply pagination + sort on avSort.valueNumeric (NULL when missing)
        String idJpql = ""
                + "SELECT p.id"
                + baseJoins
                + sortJoins
                + where
                + buildOrderBy(pageable.getSort(), /* idQuery= */ true);

        TypedQuery<Long> idQ = em.createQuery(idJpql, Long.class);
        params.forEach(idQ::setParameter);
        idQ.setFirstResult((int) pageable.getOffset());
        idQ.setMaxResults(pageable.getPageSize());
        List<Long> ids = idQ.getResultList();

        if (ids.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, total);
        }

        if (env.acceptsProfiles(Profiles.of("dev"))) {
            String formattedIds = ids.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            log.info("Retrieved product IDs: [{}]", formattedIds);
        }

        // 6) Fetch *all* attributes for those IDs
        String dataJoins = ""
                + baseJoins
                + " LEFT JOIN p.productAttributes pa"
                + " LEFT JOIN pa.attributeValue av"
                + " LEFT JOIN av.attribute a"
                + " LEFT JOIN av.measurementUnit mu";

        String dataJpql = ""
                + "SELECT"
                + "  p.id                AS id,"
                + "  p.name              AS name,"
                + "  a.name              AS attributeName,"
                + "  av.valueNumeric     AS attributeValueNumeric,"
                + "  av.valueString      AS attributeValueString,"
                + "  mu.symbol           AS measurementUnitSymbol,"
                + "  cph.price           AS price,"
                + "  b.name              AS brand,"
                + "  m.name              AS manufacturer"
                + dataJoins
                + " WHERE p.id IN :ids";

        TypedQuery<Tuple> dataQ = em.createQuery(dataJpql, Tuple.class);
        dataQ.setParameter("ids", ids);
        List<Tuple> tuples = dataQ.getResultList();

        // 7) Group into DTOs in the same order as our IDs
        LinkedHashMap<Long, ProductDtoBuilder> productMap = new LinkedHashMap<>();
        ids.forEach(id -> productMap.put(id, null));

        for (Tuple t : tuples) {
            Long pid = t.get("id", Long.class);
            var builder = productMap.get(pid);
            if (builder == null) {
                builder = new ProductDtoBuilder(
                        pid,
                        t.get("name", String.class),
                        t.get("price", BigDecimal.class),
                        t.get("brand", String.class),
                        t.get("manufacturer", String.class)
                );
                productMap.put(pid, builder);
            }
            Double value = Optional.ofNullable(t.get("attributeValueNumeric", Number.class))
                    .map(Number::doubleValue)
                    .orElse(null);
            builder.addAttribute(new AttributeDto(
                    t.get("attributeName", String.class),
                    value,
                    t.get("attributeValueString", String.class),
                    t.get("measurementUnitSymbol", String.class)
            ));
        }

        List<ProductDto> dtos = productMap.values()
                .stream()
                .map(ProductDtoBuilder::build)
                .toList();

        return new PageImpl<>(dtos, pageable, total);
    }

    private String buildOrderBy(Sort sort, boolean idQuery) {
        if (!sort.isSorted()) {
            return "";
        }
        List<String> clauses = new ArrayList<>();

        // Pre-compile once outside the loop if you like:
        Pattern pNum = Pattern.compile("^attributecriteria\\[(\\d+)\\]\\.valuenumeric$");
        Pattern pStr = Pattern.compile("^attributecriteria\\[(\\d+)\\]\\.valuestring$");

        for (Sort.Order o : sort) {
            String prop = o.getProperty().toLowerCase(Locale.ROOT);

            // 1) First, handle attributeCriteria[*].valueNumeric / .valueString
            Matcher mNum = pNum.matcher(prop);
            Matcher mStr = pStr.matcher(prop);
            if (mNum.matches() || mStr.matches()) {
                int idx = Integer.parseInt(mNum.matches() ? mNum.group(1) : mStr.group(1));
                String expr = mNum.matches()
                        ? "avSort" + idx + ".valueNumeric"
                        : "avSort" + idx + ".valueString";
                clauses.add(expr + " " + o.getDirection());
                continue;  // skip the static switch below
            }

            // 2) Otherwise, fall back to your existing cases:
            String path = switch (prop) {
                case "valuenumeric", "attributevaluenumeric" -> idQuery ? "avSort.valueNumeric" : "av.valueNumeric";
                case "valuestring", "attributevaluestring" -> idQuery ? "avSort.valueString" : "av.valueString";
                case "productattributes.attributevalue.attribute.name" -> idQuery
                        ? "paSort.attributeValue.attribute.name"
                        : "a.name";
                case "name" -> "p.name";
                case "baseprice" -> "p.basePrice";
                case "brand.name", "brand", "brandname" -> "b.name";
                case "manufacturer.name", "manufacturername", "manufacturer" -> "m.name";
                case "currentpricehistory.price", "currentprice", "price" -> "cph.price";
                default -> throw new IllegalArgumentException("Unknown sort: " + o.getProperty());
            };
            clauses.add(path + " " + o.getDirection());
        }

        return " ORDER BY " + String.join(", ", clauses);
    }


    // Builder classes unchanged…
    private static class ProductDtoBuilder {
        private final Long id;
        private final String name;
        private final BigDecimal price;
        private final String brand;
        private final String manufacturer;
        private final List<AttributeDto> attrs = new ArrayList<>();

        ProductDtoBuilder(Long id, String name, BigDecimal price, String brand, String manufacturer) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.brand = brand;
            this.manufacturer = manufacturer;
        }

        void addAttribute(AttributeDto dto) {
            attrs.add(dto);
        }

        ProductDto build() {
            return new ProductDto(id, name, price, brand, manufacturer, attrs);
        }
    }
}
