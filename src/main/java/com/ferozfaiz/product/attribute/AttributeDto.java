package com.ferozfaiz.product.attribute;

// Nested attribute DTO
public record AttributeDto(
        String attributeName,
        Double attributeValueNumeric,
        String attributeValueString,
        String measurementUnit
) {}
