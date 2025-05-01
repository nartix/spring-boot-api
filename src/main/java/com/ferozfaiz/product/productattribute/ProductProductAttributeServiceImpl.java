package com.ferozfaiz.product.productattribute;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Feroz Faiz
 */
@Service
@Transactional
public class ProductProductAttributeServiceImpl implements ProductProductAttributeService {

    private final ProductProductAttributeRepository repo;

    public ProductProductAttributeServiceImpl(ProductProductAttributeRepository repo) {
        this.repo = repo;
    }

    @Override
    public ProductProductAttribute create(ProductProductAttribute ppa) {
        return repo.save(ppa);            // INSERT → O(1)
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductProductAttribute> findAll() {
        return repo.findAll();            // SELECT * → O(n)
    }

    @Override
    @Transactional(readOnly = true)
    public ProductProductAttribute findById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ProductProductAttribute not found with id " + id
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductProductAttribute> findByProductId(Integer productId) {
        return repo.findByProductId(productId);  // uses index → O(log n)
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductProductAttribute> findByAttributeValueId(Integer attributeValueId) {
        return repo.findByAttributeValueId(attributeValueId);
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException(
                    "Cannot delete – no mapping with id " + id
            );
        }
        repo.deleteById(id);           // DELETE → O(1)
    }
}