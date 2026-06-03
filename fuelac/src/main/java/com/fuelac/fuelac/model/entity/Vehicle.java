package com.fuelac.fuelac.model.entity;

import com.fuelac.fuelac.model.enums.VehicleType;
import jakarta.persistence.*;

@Entity
@Table(name = "vehicles")
public class Vehicle extends BaseEntity {
    private Organization organization;
    private VehicleType type;
    private String brand;
    private String model;
    private Integer year;
    private String registrationNumber;
    private String garageNumber;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "varchar(255)")
    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    @Column(name = "brand", nullable = false)
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Column(name = "model", nullable = false)
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Column(name = "manufacture_year")
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Column(name = "registration_number", nullable = false, unique = true)
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    @Column(name = "garage_number")
    public String getGarageNumber() {
        return garageNumber;
    }

    public void setGarageNumber(String garageNumber) {
        this.garageNumber = garageNumber;
    }
}
