package com.fuelac.fuelac.dto.response;

import com.fuelac.fuelac.model.entity.Vehicle;
import com.fuelac.fuelac.model.enums.VehicleType;

import java.time.LocalDateTime;
import java.util.UUID;

public class VehicleResponse {

    private UUID id;
    private VehicleType type;
    private String brand;
    private String model;
    private Integer year;
    private String registrationNumber;
    private String garageNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VehicleResponse() {
    }

    public VehicleResponse(UUID id, VehicleType type, String brand, String model, Integer year, String registrationNumber, String garageNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.registrationNumber = registrationNumber;
        this.garageNumber = garageNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static VehicleResponse fromEntity(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getType(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getRegistrationNumber(),
                vehicle.getGarageNumber(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getGarageNumber() {
        return garageNumber;
    }

    public void setGarageNumber(String garageNumber) {
        this.garageNumber = garageNumber;
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
