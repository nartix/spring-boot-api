package com.ferozfaiz.product.productpricehistory;

import java.util.List;

/**
 * @author Feroz Faiz
 */
public interface ProductPriceHistoryService {

    ProductPriceHistory create(ProductPriceHistory history);

    List<ProductPriceHistory> findAll();

    ProductPriceHistory findById(Integer id);

    List<ProductPriceHistory> findByProductId(Integer productId);

    List<ProductPriceHistory> findCurrentHistories();

    ProductPriceHistory update(Integer id, ProductPriceHistory history);

    void delete(Integer id);
}
