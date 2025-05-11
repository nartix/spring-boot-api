package com.ferozfaiz.product.product;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Feroz Faiz
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public Product create(Product product) {
        return repo.save(product);              // INSERT → O(1)
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return repo.findAll();                 // SELECT * → O(n)
    }

    public Page<ProductDto> findAll(ProductFilter filter, Pageable pg) {
        return repo.findAllByFilter(filter, pg);
    }

    @Override
    @Transactional(readOnly = true)
    public Product findById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product not found with id " + id
                ));
    }

    @Override
    public Product update(Integer id, Product product) {
        Product existing = findById(id);
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setBasePrice(product.getBasePrice());
        existing.setViewCount(product.getViewCount());
        existing.setIsActive(product.getIsActive());
        existing.setSlug(product.getSlug());
        existing.setBrand(product.getBrand());
        existing.setManufacturer(product.getManufacturer());
        existing.setUpdatedAt(product.getUpdatedAt());
        return repo.save(existing);            // UPDATE → O(1)
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException(
                    "Cannot delete – no product with id " + id
            );
        }
        repo.deleteById(id);                   // DELETE → O(1)
    }

    @Override
    @Transactional(readOnly = true)
    public Product findBySlug(String slug) {
        return repo.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product not found with slug " + slug
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> searchByName(String fragment) {
        return repo.findByNameContaining(fragment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findActiveProducts() {
        return repo.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        return repo.findByBasePriceBetween(min, max);
    }
}
