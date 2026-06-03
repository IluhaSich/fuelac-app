package com.fuelac.fuelac.dto.response;

import com.fuelac.fuelac.model.entity.VehicleFuelNorms;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class VehicleFuelNormsResponse {

    private UUID id;
    private UUID vehicleId;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String description;
    private Double fuelNormPerKm;
    private Double fuelNormPerMachineHour;
    private Double fuelNormPerEngineHour;
    private Double fuelNormIdle;
    private Double fuelNormWarmUp;
    private Double fuelNormAirConditioner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VehicleFuelNormsResponse() {
    }

    public VehicleFuelNormsResponse(UUID id, UUID vehicleId, LocalDate validFrom, LocalDate validTo, String description, Double fuelNormPerKm, Double fuelNormPerMachineHour, Double fuelNormPerEngineHour, Double fuelNormIdle, Double fuelNormWarmUp, Double fuelNormAirConditioner, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static VehicleFuelNormsResponse fromEntity(VehicleFuelNorms norm) {
        if (norm == null) {
            return null;
        }
        return new VehicleFuelNormsResponse(
                norm.getId(),
                norm.getVehicle().getId(),
                norm.getValidFrom(),
                norm.getValidTo(),
                norm.getDescription(),
                norm.getFuelNormPerKm(),
                norm.getFuelNormPerMachineHour(),
                norm.getFuelNormPerEngineHour(),
                norm.getFuelNormIdle(),
                norm.getFuelNormWarmUp(),
                norm.getFuelNormAirConditioner(),
                norm.getCreatedAt(),
                norm.getUpdatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
