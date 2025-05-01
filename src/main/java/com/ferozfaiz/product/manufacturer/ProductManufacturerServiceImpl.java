package com.ferozfaiz.product.manufacturer;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Feroz Faiz
 */

@Service
@Transactional
public class ProductManufacturerServiceImpl implements ProductManufacturerService {

    private final ProductManufacturerRepository repo;

    public ProductManufacturerServiceImpl(ProductManufacturerRepository repo) {
        this.repo = repo;
    }

    @Override
    public ProductManufacturer create(ProductManufacturer manufacturer) {
        return repo.save(manufacturer);  // INSERT is O(1)
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductManufacturer> findAll() {
        return repo.findAll();         // SELECT * is O(n)
    }

    @Override
    @Transactional(readOnly = true)
    public ProductManufacturer findById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "ProductManufacturer not found with id " + id
                        )
                );
    }

    @Override
    public ProductManufacturer update(Integer id, ProductManufacturer manufacturer) {
        ProductManufacturer existing = findById(id);
        existing.setName(manufacturer.getName());
        return repo.save(existing);    // UPDATE is O(1)
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException(
                    "Cannot delete – no manufacturer with id " + id
            );
        }
        repo.deleteById(id);           // DELETE is O(1)
    }
}

