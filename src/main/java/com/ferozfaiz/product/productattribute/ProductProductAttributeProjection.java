package com.ferozfaiz.product.productattribute;

import com.ferozfaiz.product.attribute.ProductAttribute;
import com.ferozfaiz.product.attributevalue.ProductAttributeValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

/**
 * @author Feroz Faiz
 */
@Projection(name = "ppaWithFullAttr", types = ProductProductAttribute.class)
public interface ProductProductAttributeProjection {
    // still expose the raw value object:
    ProductAttributeValue getAttributeValue();

    // new method—no dots in the name!—that returns the full ProductAttribute
    @Value("#{target.attributeValueNumeric.attribute}")
    ProductAttribute getAttribute();
}