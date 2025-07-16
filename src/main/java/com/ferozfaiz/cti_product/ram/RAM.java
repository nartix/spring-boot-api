package com.ferozfaiz.cti_product.ram;

import com.ferozfaiz.cti_product.product.Product;
import jakarta.persistence.*;

@Entity
@Table(name = "cti_ram")
public class RAM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getSpeedMhz() {
        return speedMhz;
    }

    public void setSpeedMhz(Integer speedMhz) {
        this.speedMhz = speedMhz;
    }

    public String getMemoryType() {
        return memoryType;
    }

    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

    public String getFormFactor() {
        return formFactor;
    }

    public void setFormFactor(String formFactor) {
        this.formFactor = formFactor;
    }

    public Float getVoltage() {
        return voltage;
    }

    public void setVoltage(Float voltage) {
        this.voltage = voltage;
    }

    public String getLatencyCas() {
        return latencyCas;
    }

    public void setLatencyCas(String latencyCas) {
        this.latencyCas = latencyCas;
    }

    public Boolean getEcc() {
        return ecc;
    }

    public void setEcc(Boolean ecc) {
        this.ecc = ecc;
    }

    public Boolean getBuffered() {
        return buffered;
    }

    public void setBuffered(Boolean buffered) {
        this.buffered = buffered;
    }

    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Integer getModuleCount() {
        return moduleCount;
    }

    public void setModuleCount(Integer moduleCount) {
        this.moduleCount = moduleCount;
    }

    public Boolean getHeatSpreader() {
        return heatSpreader;
    }

    public void setHeatSpreader(Boolean heatSpreader) {
        this.heatSpreader = heatSpreader;
    }

    public Integer getWarrantyYears() {
        return warrantyYears;
    }

    public void setWarrantyYears(Integer warrantyYears) {
        this.warrantyYears = warrantyYears;
    }

    public String getXmpProfiles() {
        return xmpProfiles;
    }

    public void setXmpProfiles(String xmpProfiles) {
        this.xmpProfiles = xmpProfiles;
    }

    public String getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(String bandwidth) {
        this.bandwidth = bandwidth;
    }

    @Override
    public String toString() {
        return product.getName() + " - " + memoryType + " " + speedMhz + "MHz";
    }
}

