package com.ferozfaiz.product.attribute;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Feroz Faiz
 */
@Service
@Transactional
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ProductAttributeRepository repo;

    public ProductAttributeServiceImpl(ProductAttributeRepository repo) {
        this.repo = repo;
    }

    @Override
    public ProductAttribute create(ProductAttribute attribute) {
        return repo.save(attribute);   // INSERT → O(1)
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttribute> findAll() {
        return repo.findAll();         // SELECT * → O(n)
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAttribute findById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "ProductAttribute not found with id " + id
                        )
                );
    }

    @Override
    public ProductAttribute update(Integer id, ProductAttribute attribute) {
        ProductAttribute existing = findById(id);
        existing.setName(attribute.getName());
        existing.setDataType(attribute.getDataType());
        return repo.save(existing);    // UPDATE → O(1)
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException(
                    "Cannot delete – no attribute with id " + id
            );
        }
        repo.deleteById(id);           // DELETE → O(1)
    }
}
