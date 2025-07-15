package com.ferozfaiz.cti_product.storagemedia;

import com.ferozfaiz.cti_product.product.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "cti_storage_media")
public class StorageMedia {
    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "capacity_gb", nullable = false)
    private Integer capacityGb;

    // Add other storage media fields as needed

    public StorageMedia() {}

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getCapacityGb() {
        return capacityGb;
    }

    public void setCapacityGb(Integer capacityGb) {
        this.capacityGb = capacityGb;
    }
}

