package com.ferozfaiz.product.product;

// Nested attribute DTO
public record AttributeDto(
        String attributeName,
        Double attributeValue,
        String measurementUnit
) {}
