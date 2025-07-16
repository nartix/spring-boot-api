package com.ferozfaiz.cti_product.storagemedia;

import com.ferozfaiz.cti_product.product.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "cti_storage_media")
public class StorageMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_cti_storage_media_product"))
    private Product product;

    @Column(name = "capacity_gb", nullable = false)
    private Integer capacityGb;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
