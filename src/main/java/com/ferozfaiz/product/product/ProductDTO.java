package com.ferozfaiz.product.product;

import com.ferozfaiz.product.productattribute.AttributeDTO;

import java.util.List;

/**
 * @author Feroz Faiz
 */
public record ProductDTO(
        Integer id,
        String name,
        String description,
        String slug,
//        String brand,
//        String manufacturer,
        Boolean isActive,
//        Long viewCount,
        PriceHistoryDTO currentPriceHistory,
        List<AttributeDTO> attributes
        // …other needed fields…
) {}
