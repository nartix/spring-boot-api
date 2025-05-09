package com.ferozfaiz.product.productattribute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Feroz Faiz
 */
//@RepositoryRestResource( exported = false )
public interface ProductProductAttributeRepository
        extends JpaRepository<ProductProductAttribute, Integer> {

    List<ProductProductAttribute> findByProductId(Integer productId);

    List<ProductProductAttribute> findByAttributeValueId(Integer attributeValueId);
}