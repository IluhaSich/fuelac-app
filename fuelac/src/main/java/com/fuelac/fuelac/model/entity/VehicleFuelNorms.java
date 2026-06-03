package com.fuelac.fuelac.model.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "vehicle_fuel_norms")
public class VehicleFuelNorms extends BaseEntity {
    private Organization organization;
    private Vehicle vehicle;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String description;
    private Double fuelNormPerKm;
    private Double fuelNormPerMachineHour;
    private Double fuelNormPerEngineHour;
    private Double fuelNormIdle;
    private Double fuelNormWarmUp;
    private Double fuelNormAirConditioner;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @Column(name = "valid_from", nullable = false)
    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    @Column(name = "valid_to", nullable = false)
    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "fuel_norm_per_km")
    public Double getFuelNormPerKm() {
        return fuelNormPerKm;
    }

    public void setFuelNormPerKm(Double fuelNormPerKm) {
        this.fuelNormPerKm = fuelNormPerKm;
    }

    @Column(name = "fuel_norm_per_machine_hour")
    public Double getFuelNormPerMachineHour() {
        return fuelNormPerMachineHour;
    }

    public void setFuelNormPerMachineHour(Double fuelNormPerMachineHour) {
        this.fuelNormPerMachineHour = fuelNormPerMachineHour;
    }

    @Column(name = "fuel_norm_per_engine_hour")
    public Double getFuelNormPerEngineHour() {
        return fuelNormPerEngineHour;
    }

    public void setFuelNormPerEngineHour(Double fuelNormPerEngineHour) {
        this.fuelNormPerEngineHour = fuelNormPerEngineHour;
    }

    @Column(name = "fuel_norm_idle")
    public Double getFuelNormIdle() {
        return fuelNormIdle;
    }

    public void setFuelNormIdle(Double fuelNormIdle) {
        this.fuelNormIdle = fuelNormIdle;
    }

    @Column(name = "fuel_norm_warm_up")
    public Double getFuelNormWarmUp() {
        return fuelNormWarmUp;
    }

    public void setFuelNormWarmUp(Double fuelNormWarmUp) {
        this.fuelNormWarmUp = fuelNormWarmUp;
    }

    @Column(name = "fuel_norm_air_conditioner")
    public Double getFuelNormAirConditioner() {
        return fuelNormAirConditioner;
    }

    public void setFuelNormAirConditioner(Double fuelNormAirConditioner) {
        this.fuelNormAirConditioner = fuelNormAirConditioner;
    }
}
