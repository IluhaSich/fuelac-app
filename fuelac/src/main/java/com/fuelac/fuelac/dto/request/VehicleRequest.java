package com.fuelac.fuelac.dto.request;

import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.entity.Vehicle;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fuelac.fuelac.model.enums.VehicleType;

public class VehicleRequest {

    @NotNull
    private VehicleType type;
    @NotBlank
    private String brand;
    @NotBlank
    private String model;
    @Min(1900)
    private Integer year;
    @NotBlank
    private String registrationNumber;
    private String garageNumber;

    public VehicleRequest() {
    }

    public VehicleRequest(VehicleType type, String brand, String model, Integer year, String registrationNumber, String garageNumber) {
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.registrationNumber = registrationNumber;
        this.garageNumber = garageNumber;
    }

    public Vehicle toEntity() {
        Vehicle vehicle = new Vehicle();
        vehicle.setType(this.type);
        vehicle.setBrand(this.brand);
        vehicle.setModel(this.model);
        vehicle.setYear(this.year);
        vehicle.setRegistrationNumber(this.registrationNumber);
        vehicle.setGarageNumber(this.garageNumber);
        return vehicle;
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
}
