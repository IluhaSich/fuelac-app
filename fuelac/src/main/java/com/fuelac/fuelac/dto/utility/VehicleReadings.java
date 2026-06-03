package com.fuelac.fuelac.dto.utility;

public class VehicleReadings {
    private Double odometer;
    private Double fuel;

    public VehicleReadings() {}

    public VehicleReadings(Double odometer, Double fuel) {
        this.odometer = odometer;
        this.fuel = fuel;
    }

    public Double getOdometer() {
        return odometer;
    }

    public void setOdometer(Double odometer) {
        this.odometer = odometer;
    }

    public Double getFuel() {
        return fuel;
    }

    public void setFuel(Double fuel) {
        this.fuel = fuel;
    }
}
