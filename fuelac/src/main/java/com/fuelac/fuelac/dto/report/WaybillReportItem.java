package com.fuelac.fuelac.dto.report;

import java.time.LocalDateTime;

public class WaybillReportItem {
    private String waybillNumber;
    private String driverFullName;
    private LocalDateTime workStartDate;
    private LocalDateTime workEndDate;
    private Double fuelStart;
    private Double fuelEnd;
    private Double fuelRefilled;
    private Double odometerStart;
    private Double odometerEnd;
    private Double machineHoursStart;
    private Double machineHoursEnd;
    private Double engineHoursStart;
    private Double engineHoursEnd;
    private Double mileageKm;
    private Double machineHours;
    private Double engineHours;
    private Double idleTime;
    private Double engineWarmUpTime;
    private Double airConditionerTime;
    private Double fuelNormPer100Km;
    private Double fuelNormPerMachineHour;
    private Double fuelNormPerEngineHour;
    private Double fuelNormIdle;
    private Double fuelNormWarmUp;
    private Double fuelNormAirConditioner;
    private Double hoursWorked;
    private Double normativeFuel;
    private Double actualFuel;
    private Double deviation;

    public String getWaybillNumber() {
        return waybillNumber;
    }

    public void setWaybillNumber(String waybillNumber) {
        this.waybillNumber = waybillNumber;
    }

    public String getDriverFullName() {
        return driverFullName;
    }

    public void setDriverFullName(String driverFullName) {
        this.driverFullName = driverFullName;
    }

    public LocalDateTime getWorkStartDate() {
        return workStartDate;
    }

    public void setWorkStartDate(LocalDateTime workStartDate) {
        this.workStartDate = workStartDate;
    }

    public LocalDateTime getWorkEndDate() {
        return workEndDate;
    }

    public void setWorkEndDate(LocalDateTime workEndDate) {
        this.workEndDate = workEndDate;
    }

    public Double getFuelStart() {
        return fuelStart;
    }

    public void setFuelStart(Double fuelStart) {
        this.fuelStart = fuelStart;
    }

    public Double getFuelEnd() {
        return fuelEnd;
    }

    public void setFuelEnd(Double fuelEnd) {
        this.fuelEnd = fuelEnd;
    }

    public Double getFuelRefilled() {
        return fuelRefilled;
    }

    public void setFuelRefilled(Double fuelRefilled) {
        this.fuelRefilled = fuelRefilled;
    }

    public Double getOdometerStart() {
        return odometerStart;
    }

    public void setOdometerStart(Double odometerStart) {
        this.odometerStart = odometerStart;
    }

    public Double getOdometerEnd() {
        return odometerEnd;
    }

    public void setOdometerEnd(Double odometerEnd) {
        this.odometerEnd = odometerEnd;
    }

    public Double getMachineHoursStart() {
        return machineHoursStart;
    }

    public void setMachineHoursStart(Double machineHoursStart) {
        this.machineHoursStart = machineHoursStart;
    }

    public Double getMachineHoursEnd() {
        return machineHoursEnd;
    }

    public void setMachineHoursEnd(Double machineHoursEnd) {
        this.machineHoursEnd = machineHoursEnd;
    }

    public Double getEngineHoursStart() {
        return engineHoursStart;
    }

    public void setEngineHoursStart(Double engineHoursStart) {
        this.engineHoursStart = engineHoursStart;
    }

    public Double getEngineHoursEnd() {
        return engineHoursEnd;
    }

    public void setEngineHoursEnd(Double engineHoursEnd) {
        this.engineHoursEnd = engineHoursEnd;
    }

    public Double getMileageKm() {
        return mileageKm;
    }

    public void setMileageKm(Double mileageKm) {
        this.mileageKm = mileageKm;
    }

    public Double getMachineHours() {
        return machineHours;
    }

    public void setMachineHours(Double machineHours) {
        this.machineHours = machineHours;
    }

    public Double getEngineHours() {
        return engineHours;
    }

    public void setEngineHours(Double engineHours) {
        this.engineHours = engineHours;
    }

    public Double getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(Double idleTime) {
        this.idleTime = idleTime;
    }

    public Double getEngineWarmUpTime() {
        return engineWarmUpTime;
    }

    public void setEngineWarmUpTime(Double engineWarmUpTime) {
        this.engineWarmUpTime = engineWarmUpTime;
    }

    public Double getAirConditionerTime() {
        return airConditionerTime;
    }

    public void setAirConditionerTime(Double airConditionerTime) {
        this.airConditionerTime = airConditionerTime;
    }

    public Double getFuelNormPer100Km() {
        return fuelNormPer100Km;
    }

    public void setFuelNormPer100Km(Double fuelNormPer100Km) {
        this.fuelNormPer100Km = fuelNormPer100Km;
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

    public Double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(Double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public Double getNormativeFuel() {
        return normativeFuel;
    }

    public void setNormativeFuel(Double normativeFuel) {
        this.normativeFuel = normativeFuel;
    }

    public Double getActualFuel() {
        return actualFuel;
    }

    public void setActualFuel(Double actualFuel) {
        this.actualFuel = actualFuel;
    }

    public Double getDeviation() {
        return deviation;
    }

    public void setDeviation(Double deviation) {
        this.deviation = deviation;
    }
}