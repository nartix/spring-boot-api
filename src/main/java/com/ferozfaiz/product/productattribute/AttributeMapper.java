package com.ferozfaiz.product.productattribute;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Feroz Faiz
 */
public class AttributeMapper {
    public static AttributeDTO toDto(ProductProductAttribute attr) {
        var av = attr.getAttributeValue();
        return new AttributeDTO(
                attr.getId(),
                av.getAttribute().getName(),
                av.getValueString()  // or getValueNumeric(), getValueBoolean(), etc.
        );
    }

    public static List<AttributeDTO> toDtoList(List<ProductProductAttribute> attrs) {
        return attrs.stream()
                .map(AttributeMapper::toDto)
                .collect(Collectors.toList());
    }
}