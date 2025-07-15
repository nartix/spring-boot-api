package com.ferozfaiz.cti_product.harddrive;

import com.ferozfaiz.cti_product.product.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "cti_harddrive", indexes = {
        @Index(name = "idx_harddrive_capacity_gb", columnList = "capacity_gb"),
        @Index(name = "idx_harddrive_interface_type", columnList = "interface_type"),
        @Index(name = "idx_harddrive_form_factor", columnList = "form_factor"),
        @Index(name = "idx_harddrive_is_ssd", columnList = "is_ssd")
})
public class HardDrive {
    @Id
    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(name = "interface_type", length = 64, nullable = false)
    private String interfaceType;

    @Column(name = "form_factor", length = 32, nullable = false)
    private String formFactor;

    @Column(name = "rpm")
    private Integer rpm;

    @Column(name = "cache_mb")
    private Integer cacheMb;

    @Column(name = "read_speed_mbps")
    private Integer readSpeedMbps;

    @Column(name = "write_speed_mbps")
    private Integer writeSpeedMbps;

    @Column(name = "is_ssd")
    private Boolean isSsd = false;

    @Column(name = "endurance_tbw")
    private Integer enduranceTbw;

    @Column(name = "warranty_years")
    private Integer warrantyYears;

    @Column(name = "dimensions_mm", length = 64)
    private String dimensionsMm;

    @Column(name = "weight_grams")
    private Integer weightGrams;

    @Column(name = "power_consumption_watts")
    private Float powerConsumptionWatts;

    @Column(name = "shock_resistance_g")
    private Integer shockResistanceG;

    @Column(name = "temperature_range_celsius", length = 64)
    private String temperatureRangeCelsius;

    @Column(name = "mtbf_hours")
    private Integer mtbfHours;

    @Column(name = "ssd_technology", length = 20)
    private String ssdTechnology;

    @Column(name = "recording_technology", length = 20)
    private String recordingTechnology;

    @Column(name = "capacity_gb")
    private Integer capacityGb;

    // Getters and setters
    // ...

    @Override
    public String toString() {
        return product.getName() + " - " + capacityGb + "GB " + interfaceType;
    }
}

