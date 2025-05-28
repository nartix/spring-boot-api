package com.ferozfaiz.product.product;

import com.ferozfaiz.product.attribute.AttributeDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Feroz Faiz
 */
public record ProductDto(
        Long id,
        String name,
        BigDecimal price,
        String brand,
        String manufacturer,
        List<AttributeDto> attributes
) {}

