package com.fuelac.fuelac.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.UUID;

public class VehicleFuelNormsRequest {

    @NotNull
    private UUID vehicleId;
    @NotNull
    private LocalDate validFrom;
    @NotNull
    private LocalDate validTo;
    private String description;
    @PositiveOrZero
    private Double fuelNormPerKm;
    @PositiveOrZero
    private Double fuelNormPerMachineHour;
    @PositiveOrZero
    private Double fuelNormPerEngineHour;
    @PositiveOrZero
    private Double fuelNormIdle;
    @PositiveOrZero
    private Double fuelNormWarmUp;
    @PositiveOrZero
    private Double fuelNormAirConditioner;

    public VehicleFuelNormsRequest() {
    }

    public VehicleFuelNormsRequest(UUID vehicleId, LocalDate validFrom, LocalDate validTo, String description, Double fuelNormPerKm, Double fuelNormPerMachineHour, Double fuelNormPerEngineHour, Double fuelNormIdle, Double fuelNormWarmUp, Double fuelNormAirConditioner) {
        this.vehicleId = vehicleId;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.description = description;
        this.fuelNormPerKm = fuelNormPerKm;
        this.fuelNormPerMachineHour = fuelNormPerMachineHour;
        this.fuelNormPerEngineHour = fuelNormPerEngineHour;
        this.fuelNormIdle = fuelNormIdle;
        this.fuelNormWarmUp = fuelNormWarmUp;
        this.fuelNormAirConditioner = fuelNormAirConditioner;
    }

    public UUID getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(UUID vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getFuelNormPerKm() {
        return fuelNormPerKm;
    }

    public void setFuelNormPerKm(Double fuelNormPerKm) {
        this.fuelNormPerKm = fuelNormPerKm;
    }

    public Double getFuelNormPerMachineHour() {
        return fuelNormPerMachineHour;
    }

    public void setFuelNormPerMachineHour(Double fuelNormPerMachineHour) {
        this.fuelNormPerMachineHour = fuelNormPerMachineHour;
    }

    public Double getFuelNormPerEngineHour() {
        return fuelNormPerEngineHour;
    }

    public void setFuelNormPerEngineHour(Double fuelNormPerEngineHour) {
        this.fuelNormPerEngineHour = fuelNormPerEngineHour;
    }

    public Double getFuelNormIdle() {
        return fuelNormIdle;
    }

    public void setFuelNormIdle(Double fuelNormIdle) {
        this.fuelNormIdle = fuelNormIdle;
    }

    public Double getFuelNormWarmUp() {
        return fuelNormWarmUp;
    }

    public void setFuelNormWarmUp(Double fuelNormWarmUp) {
        this.fuelNormWarmUp = fuelNormWarmUp;
    }

    public Double getFuelNormAirConditioner() {
        return fuelNormAirConditioner;
    }

    public void setFuelNormAirConditioner(Double fuelNormAirConditioner) {
        this.fuelNormAirConditioner = fuelNormAirConditioner;
    }
}
