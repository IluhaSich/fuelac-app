package com.fuelac.fuelac.dto.request;

import com.fuelac.fuelac.model.enums.MessageType;
import com.fuelac.fuelac.model.enums.TransportationType;
import com.fuelac.fuelac.model.enums.VehicleType;
import com.fuelac.fuelac.model.enums.WaybillStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.UUID;

public class WaybillRequest {

    @NotBlank
    private String number;
    private WaybillStatus status;
    @NotNull
    private VehicleType vehicleType;
    private MessageType messageType;
    private TransportationType transportationType;
    private UUID driverId;
    @NotNull
    private UUID vehicleId;
    private LocalDateTime workStartDate;
    private LocalDateTime workEndDate;
    @PositiveOrZero
    private Double fuelStart;
    @PositiveOrZero
    private Double fuelEnd;
    @PositiveOrZero
    private Double fuelRefilled;
    @Positive
    private Double odometerStart;
    @Positive
    private Double odometerEnd;
    @Positive
    private Double machineHoursStart;
    @Positive
    private Double machineHoursEnd;
    @Positive
    private Double engineHoursStart;
    @Positive
    private Double engineHoursEnd;
    @PositiveOrZero
    private Double idleTime;
    @PositiveOrZero
    private Double engineWarmUpTime;
    @PositiveOrZero
    private Double airConditionerTime;
    private UUID appliedFuelNormId;
    private Double calculatedNormativeFuel;
    private Double calculatedActualFuel;
    private Double calculatedDeviation;
    private LocalDateTime closedAt;

    public WaybillRequest() {
    }

    public WaybillRequest(String number, WaybillStatus status, VehicleType vehicleType, MessageType messageType, TransportationType transportationType, UUID driverId, UUID vehicleId, LocalDateTime workStartDate, LocalDateTime workEndDate, Double fuelStart, Double fuelEnd, Double fuelRefilled, Double odometerStart, Double odometerEnd, Double machineHoursStart, Double machineHoursEnd, Double engineHoursStart, Double engineHoursEnd, Double idleTime, Double engineWarmUpTime, Double airConditionerTime, UUID appliedFuelNormId, Double calculatedNormativeFuel, Double calculatedActualFuel, Double calculatedDeviation, LocalDateTime closedAt) {
        this.number = number;
        this.status = status;
        this.vehicleType = vehicleType;
        this.messageType = messageType;
        this.transportationType = transportationType;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.workStartDate = workStartDate;
        this.workEndDate = workEndDate;
        this.fuelStart = fuelStart;
        this.fuelEnd = fuelEnd;
        this.fuelRefilled = fuelRefilled;
        this.odometerStart = odometerStart;
        this.odometerEnd = odometerEnd;
        this.machineHoursStart = machineHoursStart;
        this.machineHoursEnd = machineHoursEnd;
        this.engineHoursStart = engineHoursStart;
        this.engineHoursEnd = engineHoursEnd;
        this.idleTime = idleTime;
        this.engineWarmUpTime = engineWarmUpTime;
        this.airConditionerTime = airConditionerTime;
        this.appliedFuelNormId = appliedFuelNormId;
        this.calculatedNormativeFuel = calculatedNormativeFuel;
        this.calculatedActualFuel = calculatedActualFuel;
        this.calculatedDeviation = calculatedDeviation;
        this.closedAt = closedAt;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public WaybillStatus getStatus() {
        return status;
    }

    public void setStatus(WaybillStatus status) {
        this.status = status;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public TransportationType getTransportationType() {
        return transportationType;
    }

    public void setTransportationType(TransportationType transportationType) {
        this.transportationType = transportationType;
    }

    public UUID getDriverId() {
        return driverId;
    }

    public void setDriverId(UUID driverId) {
        this.driverId = driverId;
    }

    public UUID getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(UUID vehicleId) {
        this.vehicleId = vehicleId;
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

    public UUID getAppliedFuelNormId() {
        return appliedFuelNormId;
    }

    public void setAppliedFuelNormId(UUID appliedFuelNormId) {
        this.appliedFuelNormId = appliedFuelNormId;
    }

    public Double getCalculatedNormativeFuel() {
        return calculatedNormativeFuel;
    }

    public void setCalculatedNormativeFuel(Double calculatedNormativeFuel) {
        this.calculatedNormativeFuel = calculatedNormativeFuel;
    }

    public Double getCalculatedActualFuel() {
        return calculatedActualFuel;
    }

    public void setCalculatedActualFuel(Double calculatedActualFuel) {
        this.calculatedActualFuel = calculatedActualFuel;
    }

    public Double getCalculatedDeviation() {
        return calculatedDeviation;
    }

    public void setCalculatedDeviation(Double calculatedDeviation) {
        this.calculatedDeviation = calculatedDeviation;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
}
