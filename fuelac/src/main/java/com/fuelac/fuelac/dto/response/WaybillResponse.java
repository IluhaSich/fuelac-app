package com.fuelac.fuelac.dto.response;

import com.fuelac.fuelac.model.entity.Waybill;
import com.fuelac.fuelac.model.enums.MessageType;
import com.fuelac.fuelac.model.enums.TransportationType;
import com.fuelac.fuelac.model.enums.VehicleType;
import com.fuelac.fuelac.model.enums.WaybillStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class WaybillResponse {

    private UUID id;
    private String number;
    private WaybillStatus status;
    private VehicleType vehicleType;
    private MessageType messageType;
    private TransportationType transportationType;
    private DriverResponse driver;
    private VehicleResponse vehicle;
    private UserResponse createdBy;
    private String createdByFullName;
    private UserResponse closedBy;
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
    private Double idleTime;
    private Double engineWarmUpTime;
    private Double airConditionerTime;
    private VehicleFuelNormsResponse appliedFuelNorm;
    private Double calculatedNormativeFuel;
    private Double calculatedActualFuel;
    private Double calculatedDeviation;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WaybillResponse() {
    }

    public WaybillResponse(UUID id, String number, WaybillStatus status, VehicleType vehicleType, MessageType messageType, TransportationType transportationType, DriverResponse driver, VehicleResponse vehicle, UserResponse createdBy, UserResponse closedBy, LocalDateTime workStartDate, LocalDateTime workEndDate, Double fuelStart, Double fuelEnd, Double fuelRefilled, Double odometerStart, Double odometerEnd, Double machineHoursStart, Double machineHoursEnd, Double engineHoursStart, Double engineHoursEnd, Double idleTime, Double engineWarmUpTime, Double airConditionerTime, VehicleFuelNormsResponse appliedFuelNorm, Double calculatedNormativeFuel, Double calculatedActualFuel, Double calculatedDeviation, LocalDateTime closedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.number = number;
        this.status = status;
        this.vehicleType = vehicleType;
        this.messageType = messageType;
        this.transportationType = transportationType;
        this.driver = driver;
        this.vehicle = vehicle;
        this.createdBy = createdBy;
        this.closedBy = closedBy;
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
        this.appliedFuelNorm = appliedFuelNorm;
        this.calculatedNormativeFuel = calculatedNormativeFuel;
        this.calculatedActualFuel = calculatedActualFuel;
        this.calculatedDeviation = calculatedDeviation;
        this.closedAt = closedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static WaybillResponse fromEntity(Waybill waybill) {
        if (waybill == null) {
            return null;
        }
        WaybillResponse response = new WaybillResponse(
                waybill.getId(),
                waybill.getNumber(),
                waybill.getStatus(),
                waybill.getVehicleType(),
                waybill.getMessageType(),
                waybill.getTransportationType(),
                DriverResponse.fromEntity(waybill.getDriver()),
                VehicleResponse.fromEntity(waybill.getVehicle()),
                UserResponse.fromEntity(waybill.getCreatedBy()),
                UserResponse.fromEntity(waybill.getClosedBy()),
                waybill.getWorkStartDate(),
                waybill.getWorkEndDate(),
                waybill.getFuelStart(),
                waybill.getFuelEnd(),
                waybill.getFuelRefilled(),
                waybill.getOdometerStart(),
                waybill.getOdometerEnd(),
                waybill.getMachineHoursStart(),
                waybill.getMachineHoursEnd(),
                waybill.getEngineHoursStart(),
                waybill.getEngineHoursEnd(),
                waybill.getIdleTime(),
                waybill.getEngineWarmUpTime(),
                waybill.getAirConditionerTime(),
                VehicleFuelNormsResponse.fromEntity(waybill.getAppliedFuelNorm()),
                waybill.getCalculatedNormativeFuel(),
                waybill.getCalculatedActualFuel(),
                waybill.getCalculatedDeviation(),
                waybill.getClosedAt(),
                waybill.getCreatedAt(),
                waybill.getUpdatedAt()
        );
        UserResponse createdBy = response.getCreatedBy();
        response.setCreatedByFullName(createdBy != null ? createdBy.getFullName() : null);
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public DriverResponse getDriver() {
        return driver;
    }

    public void setDriver(DriverResponse driver) {
        this.driver = driver;
    }

    public VehicleResponse getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleResponse vehicle) {
        this.vehicle = vehicle;
    }

    public UserResponse getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserResponse createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedByFullName() {
        return createdByFullName;
    }

    public void setCreatedByFullName(String createdByFullName) {
        this.createdByFullName = createdByFullName;
    }

    public UserResponse getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(UserResponse closedBy) {
        this.closedBy = closedBy;
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

    public VehicleFuelNormsResponse getAppliedFuelNorm() {
        return appliedFuelNorm;
    }

    public void setAppliedFuelNorm(VehicleFuelNormsResponse appliedFuelNorm) {
        this.appliedFuelNorm = appliedFuelNorm;
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
