package com.ferozfaiz.product;

import com.ferozfaiz.product.attribute.AttributeDto;
import com.ferozfaiz.product.attribute.ProductAttribute;
import com.ferozfaiz.product.attributevalue.ProductAttributeValue;
import com.ferozfaiz.product.brand.ProductBrand;
import com.ferozfaiz.product.manufacturer.ProductManufacturer;
import com.ferozfaiz.product.measurementunit.ProductMeasurementUnit;
import com.ferozfaiz.product.product.*;
import com.ferozfaiz.product.productattribute.ProductProductAttribute;
import com.ferozfaiz.product.productpricehistory.ProductPriceHistory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Feroz Faiz
 */

@DataJpaTest(
        excludeAutoConfiguration = LiquibaseAutoConfiguration.class,
        properties = {
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.sql.init.mode=always",
                "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
        }
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(ProductRepositoryImpl.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductRepositoryImplTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ProductRepositoryCustom repo;

    @BeforeEach
    void setUpTestData() {
        // ——— Brands & Manufacturers ———
        ProductBrand acme = new ProductBrand("Acme");
        ProductBrand global = new ProductBrand("Global");
        em.persist(acme);
        em.persist(global);

        ProductManufacturer m1 = new ProductManufacturer("M1");
        em.persist(m1);

        // ——— Measurement Unit ———
        ProductMeasurementUnit mm = new ProductMeasurementUnit("Millimeter", "mm", 1.0);
        em.persist(mm);

        // ——— Attributes ———
        ProductAttribute color = new ProductAttribute("Color", "string");
        ProductAttribute width = new ProductAttribute("Width", "numeric");
        ProductAttribute size = new ProductAttribute("Size", "numeric");
        em.persist(color);
        em.persist(width);
        em.persist(size);

        // ——— Products ———
        Product p1 = new Product("Widget", BigDecimal.valueOf(10), true, acme, m1);
        Product p2 = new Product("Gadget", BigDecimal.valueOf(12), true, global, m1);
        Product p3 = new Product("Doodad", BigDecimal.valueOf(8), true, acme, m1);
        Product p4 = new Product("Thingamajig", BigDecimal.valueOf(5), true, global, m1);
        Product p5 = new Product("Whatsit", BigDecimal.valueOf(7), true, acme, m1);
        em.persist(p1);
        em.persist(p2);
        em.persist(p3);
        em.persist(p4);
        em.persist(p5);

        // ——— AttributeValues & Associations ———
        // helper to keep it DRY
        BiFunction<ProductAttribute, ProductMeasurementUnit, ProductAttributeValue> makeAV =
                (attr, unit) -> {
                    ProductAttributeValue pav = new ProductAttributeValue();
                    pav.setAttribute(attr);           // ← make sure attribute_id is set
                    pav.setMeasurementUnit(unit);     // may be null if unit==null
                    em.persist(pav);
                    return pav;
                };

        // Color values
        ProductAttributeValue redVal = makeAV.apply(color, null);
        redVal.setValueString("Red");
        ProductAttributeValue blueVal = makeAV.apply(color, null);
        blueVal.setValueString("Blue");

        em.persist(new ProductProductAttribute(p1, redVal));
        em.persist(new ProductProductAttribute(p2, redVal));
        em.persist(new ProductProductAttribute(p3, blueVal));

        // Width values
        ProductAttributeValue w5 = makeAV.apply(width, mm);
        w5.setValueNumeric(5.0);
        ProductAttributeValue w10 = makeAV.apply(width, mm);
        w10.setValueNumeric(10.0);

        em.persist(new ProductProductAttribute(p1, w5));
        em.persist(new ProductProductAttribute(p4, w5));
        em.persist(new ProductProductAttribute(p2, w10));

        // Size values
        ProductAttributeValue sM2 = makeAV.apply(size, mm);
        sM2.setValueNumeric(2.0);
        sM2.setValueString("M");
        ProductAttributeValue sL3 = makeAV.apply(size, mm);
        sL3.setValueNumeric(3.0);
        sL3.setValueString("L");

        // ——— Price History ———
        LocalDateTime now = LocalDateTime.now();

        // p1 history + current
        em.persist(new ProductPriceHistory(p1,
                BigDecimal.valueOf(8.50), now.minusDays(30), now.minusDays(15), false));
        em.persist(new ProductPriceHistory(p1,
                BigDecimal.valueOf(9.50), now.minusDays(15), now.minusDays(1), false));
        em.persist(new ProductPriceHistory(p1,
                BigDecimal.valueOf(10.00), now.minusDays(1), null, true));

        // p2 history + current
        em.persist(new ProductPriceHistory(p2,
                BigDecimal.valueOf(11.00), now.minusDays(20), now.minusDays(5), false));
        em.persist(new ProductPriceHistory(p2,
                BigDecimal.valueOf(12.00), now.minusDays(5), null, true));

        // p3 history + current
        em.persist(new ProductPriceHistory(p3,
                BigDecimal.valueOf(7.00), now.minusDays(25), now.minusDays(10), false));
        em.persist(new ProductPriceHistory(p3,
                BigDecimal.valueOf(8.00), now.minusDays(10), null, true));

        // p4 history + current
        em.persist(new ProductPriceHistory(p4,
                BigDecimal.valueOf(5.00), now.minusDays(7), now.minusDays(2), false));
        em.persist(new ProductPriceHistory(p4,
                BigDecimal.valueOf(5.50), now.minusDays(2), null, true));

        // p5 is intentionally left without any ProductPriceHistory records

        em.persist(new ProductProductAttribute(p1, sM2));
        em.persist(new ProductProductAttribute(p2, sL3));

        em.flush();
        em.clear();
    }
    // path: /products
    @Test
    @Order(11)
    @DisplayName("1.1 No filters returns all products")
    void whenNoFilters_thenAllProductsReturned() {
        ProductFilter filter = new ProductFilter();
        Pageable page = PageRequest.of(0, 10);
        Page<ProductDto> result = repo.findAllByFilter(filter, page);

        // assume we inserted 5 products
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(5);
    }

    // path /products?name=wid&page=0&size=10
    @Test
    @Order(21)
    @DisplayName("2.1 Name filter is case‐insensitive substring")
    void whenNameFilter_thenOnlyMatchesReturned() {
        var filter = new ProductFilter();
        filter.setName("wid");  // matches "Widget"
        Page<ProductDto> page = repo.findAllByFilter(filter, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).name()).isEqualTo("Widget");
    }

    // path /products?name=Gadget&page=0&size=10
    @Test
    @Order(22)
    @DisplayName("2.2 Exact name match returns the matching product")
    void whenNameFilterExact_thenOnlyMatchesReturned() {
        var filter = new ProductFilter();
        filter.setName("Gadget");
        Page<ProductDto> page = repo.findAllByFilter(filter, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).name()).isEqualTo("Gadget");
    }

    // path /products?page=0&size=1
    @Test
    @Order(31)
    @DisplayName("3.1 Page size 1 yields five pages of one product each")
    void whenPageSize1_then5PagesReturned() {
        var filter = new ProductFilter();
        Page<ProductDto> page = repo.findAllByFilter(filter, PageRequest.of(0, 1));

        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalPages()).isEqualTo(5);
    }

    // path /products?page=1&size=2
    @Test
    @Order(32)
    @DisplayName("3.2 Page 1 with size 2 returns 2 products")
    void whenPageSize2_then2ProductsOnPage1() {
        var filter = new ProductFilter();
        Page<ProductDto> page = repo.findAllByFilter(filter, PageRequest.of(1, 2));

        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalPages()).isEqualTo(3);
    }

    // path /products?page=0&size=1&sort=name,asc
    @Test
    @Order(33)
    @DisplayName("3.3 Returns Doodad as the only product for page 0 (size=1) sorted by name ascending")
    void whenPageSize1AndSortByNameAsc_thenDoodadFirst() {
        var filter = new ProductFilter();
        Pageable page = PageRequest.of(0, 1, Sort.by("name").ascending());
        Page<ProductDto> result = repo.findAllByFilter(filter, page);

        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Doodad");
    }

    // path /products?page=0&size=1&sort=name,desc
    @Test
    @Order(34)
    @DisplayName("3.4 Returns Widget as the single product for page 0 (size=1) sorted by name descending")
    void whenPageSize1AndSortByNameDesc_thenWhatsitFirst() {
        var filter = new ProductFilter();
        Pageable page = PageRequest.of(0, 1, Sort.by("name").descending());
        Page<ProductDto> result = repo.findAllByFilter(filter, page);

        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Widget");
    }

//    @Test
//    @DisplayName("3.3 Between min & max price")
//    void whenPriceRange_thenCorrectProducts() {
//        var filter = new ProductFilter();
//        filter.setMinBasePrice(BigDecimal.valueOf(5.0));
//        filter.setMaxBasePrice(BigDecimal.valueOf(15.0));
//        Page<ProductDto> page = repo.findAllByFilter(filter, PageRequest.of(0, 10));
//
//        // e.g. products priced 10 and 12 match
//        assertThat(page.getContent()).extracting("basePrice")
//                .allMatch(p -> p >= 5.0 && p <= 15.0);
//    }

    // path /products?attributeName=Color&attributeValueString=Red
    @Test
    @Order(61)
    @DisplayName("6.1 When filtering by Color='Red', returns only the two red products")
    void whenAttributeStringFilter_thenCorrectProducts() {
        var filter = new ProductFilter();
        filter.setAttributeName("Color");
        filter.setAttributeValueString(List.of("Red"));
        Page<ProductDto> page = repo.findAllByFilter(filter, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2); // two products are red
        page.getContent().forEach(dto ->
                assertThat(dto.attributes())
                        .extracting(AttributeDto::attributeName, AttributeDto::attributeValueString)
                        .contains(tuple("Color", "Red"))
        );
    }

    // path /products?attributeName=Width&attributeValueNumeric=5&attributeValueNumeric=10
    @Test
    @Order(72)
    @DisplayName("7.2 When filtering by Width = 5 and 10, returns only products with those widths")
    void whenAttributeNumericFilter_thenCorrectProducts() {
        var filter = new ProductFilter();
        filter.setAttributeName("Width");
        filter.setAttributeValueNumeric(List.of(
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(10)
        ));
        Page<ProductDto> page = repo.findAllByFilter(filter, PageRequest.of(0, 10));

        assertThat(page.getContent())
                .allSatisfy(dto -> {
                    var widths = dto.attributes().stream()
                            .filter(a -> a.attributeName().equals("Width"))
                            .map(AttributeDto::attributeValueNumeric)
                            .toList();
                    assertThat(widths).anyMatch(n -> n == 5.0 || n == 10.0);
                });
    }

    // path /products?attributeName=Size&attributeValueString=M&attributeValueNumeric=2
    @Test
    @Order(81)
    @DisplayName("8.1 When filtering Size='M' and numeric Size=2, returns exactly one matching product")
    void whenCombinedAttrFilters_thenIntersectedResults() {
        var filter = new ProductFilter();
        filter.setAttributeName("Size");
        filter.setAttributeValueString(List.of("M"));
        filter.setAttributeValueNumeric(List.of(BigDecimal.valueOf(2)));
        Page<ProductDto> page = repo.findAllByFilter(filter, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);

        var attrs = page.getContent().get(0).attributes();

        // string match
        assertThat(attrs)
                .extracting(AttributeDto::attributeName, AttributeDto::attributeValueString)
                .contains(tuple("Size", "M"));

        // numeric match
        assertThat(attrs)
                .extracting(AttributeDto::attributeName, AttributeDto::attributeValueNumeric)
                .contains(tuple("Size", 2.0));
    }

    // path /products?attributeName=Width&attributeValueNumeric=5&attributeValueNumeric=10&sort=valueNumeric,desc
    @Test
    @Order(91)
    @DisplayName("9.1 Sort by valueNumeric desc, for AttributeName=Width")
    void whenSortByNumericDesc_thenCorrectOrder() {
        var filter = new ProductFilter();
        filter.setAttributeName("Width");
        // only those products whose Width is 10.0 or 5.0
        filter.setAttributeValueNumeric(List.of(BigDecimal.valueOf(10.0), BigDecimal.valueOf(5.0)));

        Pageable page = PageRequest.of(0, 10, Sort.by("valueNumeric").descending());
        List<ProductDto> dtos = repo.findAllByFilter(filter, page).getContent();

        // pull out the Width numbers
        List<Double> nums = dtos.stream()
                .map(dto -> dto.attributes().stream()
                        .filter(a -> "Width".equals(a.attributeName()))
                        .findFirst().get()               // now guaranteed present
                        .attributeValueNumeric()
                )
                .toList();

        // build a sorted‐descending copy at runtime
        List<Double> sortedDesc = new ArrayList<>(nums);
        sortedDesc.sort(Comparator.reverseOrder());

        // and assert they came back already in that same order
        assertThat(nums)
                .withFailMessage("Expected %s to already be in descending order", nums)
                .isEqualTo(sortedDesc);
    }

    // path /products?attributeName=Width&attributeValueNumeric=5&attributeValueNumeric=10&sort=valueNumeric,asc
    @Test
    @Order(92)
    @DisplayName("9.2 Sort by valueNumeric asc, for AttributeName=Width")
    void whenSortByNumericAsc_thenCorrectOrder() {
        var filter = new ProductFilter();
        filter.setAttributeName("Width");
        // only products with Width = 10.0 or 5.0
        filter.setAttributeValueNumeric(List.of(BigDecimal.valueOf(10.0), BigDecimal.valueOf(5.0)));

        Pageable page = PageRequest.of(0, 10, Sort.by("valueNumeric").ascending());
        List<ProductDto> dtos = repo.findAllByFilter(filter, page).getContent();

        // extract each DTO’s Width numeric
        List<Double> nums = dtos.stream()
                .map(dto -> dto.attributes().stream()
                        .filter(a -> "Width".equals(a.attributeName()))
                        .findFirst().get()                     // now guaranteed present
                        .attributeValueNumeric()
                )
                .toList();

        // build an ascending‐sorted copy
        List<Double> sortedAsc = new ArrayList<>(nums);
        sortedAsc.sort(Comparator.naturalOrder());

        // assert the returned order already matches
        assertThat(nums)
                .withFailMessage("Expected %s to already be in ascending order", nums)
                .isEqualTo(sortedAsc);
    }

    // path /products?attributeName=Width&sort=valueNumeric,desc,name,asc
    @Test
    @Order(93)
    @DisplayName("9.3 Sort by valueNumeric desc, for AttributeName=Width and sort by name, first result is Gadget, first product have no attribute width")
    void whenSortByNameAndNumericDesc_thenCorrectOrder() {
        var filter = new ProductFilter();
        filter.setAttributeName("Width");

        Pageable page = PageRequest.of(0, 10, Sort.by("valueNumeric").descending().and(Sort.by("name").ascending()));
        List<ProductDto> dtos = repo.findAllByFilter(filter, page).getContent();

        // check for name == Gadget
        assertThat(dtos.get(0).name()).isEqualTo("Gadget");
    }

    // path /products?sort=price,desc
    @Test
    @Order(101)
    @DisplayName("10.1 Sort by price descending, last is null")
    void whenSortByPriceDesc_thenCorrectOrder() {
        var filter = new ProductFilter();
        Pageable page = PageRequest.of(0, 10, Sort.by("price").descending());
        List<ProductDto> dtos = repo.findAllByFilter(filter, page).getContent();

        List<BigDecimal> prices = dtos.stream()
                .map(ProductDto::price)
                .toList();

        // sort nulls last, then reverse
        List<BigDecimal> expected = new ArrayList<>(prices);
        expected.sort(Comparator.nullsLast(Comparator.reverseOrder()));

        assertThat(prices)
                .withFailMessage("Expected %s to be in descending order with nulls last", prices)
                .isEqualTo(expected);

        // the last record should be the one with a null price
        assertThat(prices.get(prices.size() - 1))
                .withFailMessage("Expected last price to be null but was %s", prices.get(prices.size() - 1))
                .isNull();
    }

    // path /products?sort=price,asc
    @Test
    @Order(102)
    @DisplayName("10.2 Sort by price ascending, first is null")
    void whenSortByPriceAsc_thenCorrectOrder() {
        var filter = new ProductFilter();
        Pageable page = PageRequest.of(0, 10, Sort.by("price").ascending());
        List<ProductDto> dtos = repo.findAllByFilter(filter, page).getContent();

        List<BigDecimal> prices = dtos.stream()
                .map(ProductDto::price)
                .toList();

        // the null‐priced record should come first
        assertThat(prices.get(0)).isNull();

        // all non‐null prices should be in ascending order
        List<BigDecimal> nonNull = prices.stream()
                .filter(Objects::nonNull)
                .toList();
        List<BigDecimal> sortedAsc = new ArrayList<>(nonNull);
        sortedAsc.sort(Comparator.naturalOrder());
        assertThat(nonNull)
                .withFailMessage("Expected non‐null prices %s to be in ascending order", nonNull)
                .isEqualTo(sortedAsc);
    }

    // path /products?sort=price,desc,name,asc
    @Test
    @Order(103)
    @DisplayName("10.3 Sort by price desc then name asc, first is Gadget")
    void whenSortByPriceDescAndNameAsc_thenCorrectOrder() {
        var filter = new ProductFilter();
        Pageable page = PageRequest.of(0, 10,
                Sort.by("price").descending().and(Sort.by("name").ascending()));
        List<ProductDto> dtos = repo.findAllByFilter(filter, page).getContent();

        assertThat(dtos.get(0).name()).isEqualTo("Gadget");
    }

    // path /product?name=__NO_SUCH__&page=0&size=10
    @Test
    @Order(111)
    @DisplayName("11.1 No matches yields empty page")
    void whenNoMatches_thenEmptyPage() {
        var filter = new ProductFilter();
        filter.setName("__NO_SUCH__");
        Page<ProductDto> page = repo.findAllByFilter(filter, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isZero();
        assertThat(page.getContent()).isEmpty();
    }

    // path /products?sort=BogusProperty,asc
    @Test
    @Order(131)
    @DisplayName("13.1 Unknown sort property throws")
    void whenUnknownSort_thenIAE() {
        var filter = new ProductFilter();
        Pageable page = PageRequest.of(0, 10, Sort.by("bogusProperty"));
        assertThrows(IllegalArgumentException.class, () ->
                repo.findAllByFilter(filter, page)
        );
    }

}

