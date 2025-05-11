package com.ferozfaiz.product.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Feroz Faiz
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository repo;
    private final ProductService svc;

    public ProductController(ProductRepository repo, ProductService svc) {
        this.repo = repo;
        this.svc = svc;
    }

    //    @GetMapping
//    public Page<Product> listProducts(
//            @RequestParam Map<String, String[]> allParams,
//            @PageableDefault(page = 0, size = 20) Pageable pageable
//    ) {
//        // Build a single Specification that:
//        //  1) fetches all associations (for the main query)
//        //  2) applies dynamic filters
//        Specification<Product> spec = ProductSpecs.build(allParams);
//
//        // this executes exactly two queries:
//        // 1. count query
//        // 2. select with all joins, where, order, limit/offset
//        return repo.findAll(spec, pageable);
//    }
//    @GetMapping
//    public Page<ProductDTO> listProducts(
//            @RequestParam Map<String, String[]> allParams,
//            @PageableDefault(page = 0, size = 20) Pageable pageable
//    ) {
//        Specification<Product> spec = ProductSpecs.build(allParams);
//        return repo.findAll(spec, pageable)
//                .map(this::toDto);
//    }
//
//    private ProductDTO toDto(Product p) {
//        var cph = p.getCurrentPriceHistory();
//        PriceHistoryDTO phDto = new PriceHistoryDTO(
//                cph.getId(),
//                cph.getPrice(),
//                cph.getStartDate(),
//                cph.getEndDate()
//        );
//        var attrDtos = AttributeMapper.toDtoList(
//                new ArrayList<>(p.getProductAttributes())
//        );
//        return new ProductDTO(
//                p.getId(),
//                p.getName(),
//                p.getDescription(),
//                p.getSlug(),
//                p.getIsActive(),
//                phDto,
//                attrDtos
//                // …other fields…
//        );
//    }

    @GetMapping
    public Page<ProductDto> list(
            ProductFilter filter,
            @PageableDefault(size = 20) Pageable page
    ) {
        return svc.findAll(filter, page);
    }
}

