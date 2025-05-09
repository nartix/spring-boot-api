package com.ferozfaiz.product.product;

import java.math.BigDecimal;

public record PriceHistoryDTO(
        Integer id,
        BigDecimal price,
        java.time.LocalDateTime startDate,
        java.time.LocalDateTime endDate
) {}
