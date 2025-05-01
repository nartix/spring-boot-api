package com.ferozfaiz.product.measurementunit;

import com.ferozfaiz.product.brand.ProductBrand;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Feroz Faiz
 */
@Service
@Transactional
public class ProductBrandServiceImpl implements ProductBrandService {

    private final ProductBrandRepository repo;

    public ProductBrandServiceImpl(ProductBrandRepository repo) {
        this.repo = repo;
    }

    @Override
    public ProductBrand create(ProductBrand brand) {
        return repo.save(brand);  // INSERT → O(1)
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductBrand> findAll() {
        return repo.findAll();    // SELECT * → O(n)
    }

    @Override
    @Transactional(readOnly = true)
    public ProductBrand findById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("ProductBrand not found with id " + id)
                );
    }

    @Override
    public ProductBrand update(Integer id, ProductBrand brand) {
        ProductBrand existing = findById(id);
        existing.setName(brand.getName());
        return repo.save(existing); // UPDATE → O(1)
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete – no brand with id " + id);
        }
        repo.deleteById(id);        // DELETE → O(1)
    }
}
