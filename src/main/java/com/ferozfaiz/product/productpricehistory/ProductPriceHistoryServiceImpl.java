package com.ferozfaiz.product.productpricehistory;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Feroz Faiz
 */
@Service
@Transactional
public class ProductPriceHistoryServiceImpl implements ProductPriceHistoryService {

    private final ProductPriceHistoryRepository repo;

    public ProductPriceHistoryServiceImpl(ProductPriceHistoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public ProductPriceHistory create(ProductPriceHistory history) {
        return repo.save(history);              // INSERT → O(1)
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPriceHistory> findAll() {
        return repo.findAll();                  // SELECT * → O(n)
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPriceHistory findById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "PriceHistory not found with id " + id
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPriceHistory> findByProductId(Integer productId) {
        return repo.findByProductIdOrderByStartDateDesc(productId);  // O(log n) + O(k)
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPriceHistory> findCurrentHistories() {
        return repo.findByIsCurrentTrue();      // O(log n) + O(k)
    }

    @Override
    public ProductPriceHistory update(Integer id, ProductPriceHistory history) {
        ProductPriceHistory existing = findById(id);
        existing.setPrice(history.getPrice());
        existing.setStartDate(history.getStartDate());
        existing.setEndDate(history.getEndDate());
        existing.setIsCurrent(history.getIsCurrent());
        return repo.save(existing);             // UPDATE → O(1)
    }

    @Override
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException(
                    "Cannot delete – no PriceHistory with id " + id
            );
        }
        repo.deleteById(id);                    // DELETE → O(1)
    }
}