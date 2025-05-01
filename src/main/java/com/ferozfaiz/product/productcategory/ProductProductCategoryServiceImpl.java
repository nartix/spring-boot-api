package com.ferozfaiz.product.productcategory;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Feroz Faiz
 */
@Service
@Transactional
public class ProductProductCategoryServiceImpl implements ProductProductCategoryService {

    private final ProductProductCategoryRepository repo;

    public ProductProductCategoryServiceImpl(ProductProductCategoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public ProductProductCategory create(ProductProductCategory ppc) {
        return repo.save(ppc);             // INSERT → O(1)
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductProductCategory> findAll() {
        return repo.findAll();             // SELECT * → O(n)
    }

    @Override
    @Transactional(readOnly = true)
    public ProductProductCategory findById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "ProductProductCategory not found with id " + id
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductProductCategory> findByProductId(Integer productId) {
        return repo.findByProductId(productId);  // uses index → O(log n)
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductProductCategory> findByCategoryId(Integer categoryId) {
        return repo.findByCategoryId(categoryId); // uses index → O(log n)
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException(
                    "Cannot delete – no mapping with id " + id
            );
        }
        repo.deleteById(id);              // DELETE → O(1)
    }
}