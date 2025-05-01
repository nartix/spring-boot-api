package com.ferozfaiz.product.productpricehistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Feroz Faiz
 */
public interface ProductPriceHistoryRepository
        extends JpaRepository<ProductPriceHistory, Integer> {

    /**
     * Returns all history entries for a product, ordered by startDate descending
     * (uses the product_id,start_date index)
     */
    List<ProductPriceHistory> findByProductIdOrderByStartDateDesc(Integer productId);

    /**
     * Returns all entries that are currently active (is_current = true).
     */
    List<ProductPriceHistory> findByIsCurrentTrue();
}
