package com.ferozfaiz.product.product;

import com.ferozfaiz.product.attribute.AttributeDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Feroz Faiz
 */
//public record ProductDTO(
//        Integer id,
//        String name,
//        String description,
//        String slug,
////        String brand,
////        String manufacturer,
//        Boolean isActive,
////        Long viewCount,
//        PriceHistoryDTO currentPriceHistory,
//        List<AttributeDTO> attributes
//        // …other needed fields…
//) {}


// Top‐level DTO
public record ProductDto(
        Long id,
        String name,
        BigDecimal price,
        String brand,
        String manufacturer,
        List<AttributeDto> attributes
) {}

