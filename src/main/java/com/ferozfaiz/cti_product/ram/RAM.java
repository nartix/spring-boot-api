package com.ferozfaiz.cti_product.ram;

import com.ferozfaiz.cti_product.product.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "cti_ram")
public class RAM {
    @Id
    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(name = "speed_mhz", nullable = false)
    private Integer speedMhz;

    @Column(name = "memory_type", length = 32, nullable = false)
    private String memoryType;

    @Column(name = "form_factor", length = 32, nullable = false)
    private String formFactor;

    @Column(name = "voltage")
    private Float voltage;

    @Column(name = "latency_cas", length = 16)
    private String latencyCas;

    @Column(name = "ecc")
    private Boolean ecc = false;

    @Column(name = "buffered")
    private Boolean buffered = false;

    @Column(name = "registered")
    private Boolean registered = false;

    @Column(name = "rank", length = 32)
    private String rank;

    @Column(name = "module_count")
    private Integer moduleCount = 1;

    @Column(name = "heat_spreader")
    private Boolean heatSpreader = false;

    @Column(name = "warranty_years")
    private Integer warrantyYears;

    @Column(name = "xmp_profiles", length = 50)
    private String xmpProfiles;

    @Column(name = "bandwidth", length = 20)
    private String bandwidth;

    @Column(name = "capacity_gb")
    private Integer capacityGb;

    // Getters and setters
    // ...

    @Override
    public String toString() {
        return product.getName() + " - " + capacityGb + "GB " + memoryType + " " + speedMhz + "MHz";
    }
}

