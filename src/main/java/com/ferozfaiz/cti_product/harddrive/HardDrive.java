package com.ferozfaiz.cti_product.harddrive;

import com.ferozfaiz.cti_product.product.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "cti_harddrive", indexes = {
        @Index(name = "idx_harddrive_interface_type", columnList = "interface_type"),
        @Index(name = "idx_harddrive_form_factor", columnList = "form_factor"),
        @Index(name = "idx_harddrive_is_ssd", columnList = "is_ssd")
})
public class HardDrive {
    @Id
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_cti_harddrive_product"))
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

    @Column(name = "is_ssd", nullable = false)
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getFormFactor() {
        return formFactor;
    }

    public void setFormFactor(String formFactor) {
        this.formFactor = formFactor;
    }

    public Integer getRpm() {
        return rpm;
    }

    public void setRpm(Integer rpm) {
        this.rpm = rpm;
    }

    public Integer getCacheMb() {
        return cacheMb;
    }

    public void setCacheMb(Integer cacheMb) {
        this.cacheMb = cacheMb;
    }

    public Integer getReadSpeedMbps() {
        return readSpeedMbps;
    }

    public void setReadSpeedMbps(Integer readSpeedMbps) {
        this.readSpeedMbps = readSpeedMbps;
    }

    public Integer getWriteSpeedMbps() {
        return writeSpeedMbps;
    }

    public void setWriteSpeedMbps(Integer writeSpeedMbps) {
        this.writeSpeedMbps = writeSpeedMbps;
    }

    public Boolean getSsd() {
        return isSsd;
    }

    public void setSsd(Boolean ssd) {
        isSsd = ssd;
    }

    public Integer getEnduranceTbw() {
        return enduranceTbw;
    }

    public void setEnduranceTbw(Integer enduranceTbw) {
        this.enduranceTbw = enduranceTbw;
    }

    public Integer getWarrantyYears() {
        return warrantyYears;
    }

    public void setWarrantyYears(Integer warrantyYears) {
        this.warrantyYears = warrantyYears;
    }

    public String getDimensionsMm() {
        return dimensionsMm;
    }

    public void setDimensionsMm(String dimensionsMm) {
        this.dimensionsMm = dimensionsMm;
    }

    public Integer getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(Integer weightGrams) {
        this.weightGrams = weightGrams;
    }

    public Float getPowerConsumptionWatts() {
        return powerConsumptionWatts;
    }

    public void setPowerConsumptionWatts(Float powerConsumptionWatts) {
        this.powerConsumptionWatts = powerConsumptionWatts;
    }

    public Integer getShockResistanceG() {
        return shockResistanceG;
    }

    public void setShockResistanceG(Integer shockResistanceG) {
        this.shockResistanceG = shockResistanceG;
    }

    public String getTemperatureRangeCelsius() {
        return temperatureRangeCelsius;
    }

    public void setTemperatureRangeCelsius(String temperatureRangeCelsius) {
        this.temperatureRangeCelsius = temperatureRangeCelsius;
    }

    public Integer getMtbfHours() {
        return mtbfHours;
    }

    public void setMtbfHours(Integer mtbfHours) {
        this.mtbfHours = mtbfHours;
    }

    public String getSsdTechnology() {
        return ssdTechnology;
    }

    public void setSsdTechnology(String ssdTechnology) {
        this.ssdTechnology = ssdTechnology;
    }

    public String getRecordingTechnology() {
        return recordingTechnology;
    }

    public void setRecordingTechnology(String recordingTechnology) {
        this.recordingTechnology = recordingTechnology;
    }

    @Override
    public String toString() {
        return product.getName() + " - " + interfaceType;
    }
}
