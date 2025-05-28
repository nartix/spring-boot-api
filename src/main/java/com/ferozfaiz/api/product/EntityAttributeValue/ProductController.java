package com.ferozfaiz.api.product.EntityAttributeValue;

import com.ferozfaiz.product.product.ProductDto;
import com.ferozfaiz.product.product.ProductFilter;
import com.ferozfaiz.product.product.ProductRepository;
import com.ferozfaiz.product.product.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
*
* @author Feroz Faiz
* 
*/
@Validated
@RestController(value = "EntityAttributeValueProductController")
@RequestMapping("${spring.data.rest.basePath}/eav/products")
public class ProductController {
    private final ProductRepository repo;
    private final ProductService svc;

    public ProductController(ProductRepository repo, ProductService svc) {
        this.repo = repo;
        this.svc = svc;
    }

    @GetMapping
    public Page<ProductDto> list(
            ProductFilter filter,
            @PageableDefault(size = 20) Pageable page
    ) {
        return svc.findAll(filter, page);
    }
}
