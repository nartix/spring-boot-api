package com.ferozfaiz.product.product;

// Nested attribute DTO
public record AttributeDto(
        String attributeName,
        Double attributeValueNumeric,
        String attributeValueString,
        String measurementUnit
) {}
