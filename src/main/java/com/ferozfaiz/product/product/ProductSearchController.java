package com.ferozfaiz.product.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Feroz Faiz
 */
@RepositoryRestController
//@RequestMapping("/api/v1/product")
public class ProductSearchController {

    private final ProductRepository repository;

    @Autowired
    public ProductSearchController(ProductRepository repository) {
        this.repository = repository;
    }

    @RequestMapping("/api/v1/product")
    public ResponseEntity<PagedModel<EntityModel<Product>>> searchByName(
            @RequestParam(value = "name", required = false) String name,
            Pageable pageable,
            PagedResourcesAssembler<Product> assembler) {

        // build the spec
        Specification<Product> spec = ProductSpecifications.nameContains(name);

        // execute query with pagination
        Page<Product> page = repository.findAll(spec, pageable);

        // assemble HATEOAS response
        PagedModel<EntityModel<Product>> model = assembler.toModel(page);
        return ResponseEntity.ok(model);
    }
}
