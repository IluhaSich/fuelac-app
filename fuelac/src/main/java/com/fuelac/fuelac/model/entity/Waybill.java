package com.fuelac.fuelac.model.entity;

import com.fuelac.fuelac.model.enums.MessageType;
import com.fuelac.fuelac.model.enums.TransportationType;
import com.fuelac.fuelac.model.enums.VehicleType;
import com.fuelac.fuelac.model.enums.WaybillStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "waybills")
public class Waybill extends BaseEntity {
    private Organization organization;
    private String number;
    private WaybillStatus status;
    private VehicleType vehicleType;
    private MessageType messageType;
    private TransportationType transportationType;
    private Driver driver;
    private Vehicle vehicle;
    private User createdBy;
    private User closedBy;
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
    private VehicleFuelNorms appliedFuelNorm;
    private Double calculatedNormativeFuel;
    private Double calculatedActualFuel;
    private Double calculatedDeviation;
    private LocalDateTime closedAt;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @Column(name = "number", nullable = false)
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(255)")
    public WaybillStatus getStatus() {
        return status;
    }

    public void setStatus(WaybillStatus status) {
        this.status = status;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", columnDefinition = "varchar(255)")
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", columnDefinition = "varchar(255)")
    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "transportation_type", columnDefinition = "varchar(255)")
    public TransportationType getTransportationType() {
        return transportationType;
    }

    public void setTransportationType(TransportationType transportationType) {
        this.transportationType = transportationType;
    }

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @ManyToOne
    @JoinColumn(name = "closed_by_user_id")
    public User getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(User closedBy) {
        this.closedBy = closedBy;
    }

    @Column(name = "work_start_date", nullable = false)
    public LocalDateTime getWorkStartDate() {
        return workStartDate;
    }

    public void setWorkStartDate(LocalDateTime workStartDate) {
        this.workStartDate = workStartDate;
    }

    @Column(name = "work_end_date")
    public LocalDateTime getWorkEndDate() {
        return workEndDate;
    }

    public void setWorkEndDate(LocalDateTime workEndDate) {
        this.workEndDate = workEndDate;
    }

    @Column(name = "fuel_start")
    public Double getFuelStart() {
        return fuelStart;
    }

    public void setFuelStart(Double fuelStart) {
        this.fuelStart = fuelStart;
    }

    @Column(name = "fuel_end")
    public Double getFuelEnd() {
        return fuelEnd;
    }

    public void setFuelEnd(Double fuelEnd) {
        this.fuelEnd = fuelEnd;
    }

    @Column(name = "fuel_refilled")
    public Double getFuelRefilled() {
        return fuelRefilled;
    }

    public void setFuelRefilled(Double fuelRefilled) {
        this.fuelRefilled = fuelRefilled;
    }

    @Column(name = "odometer_start")
    public Double getOdometerStart() {
        return odometerStart;
    }

    public void setOdometerStart(Double odometerStart) {
        this.odometerStart = odometerStart;
    }

    @Column(name = "odometer_end")
    public Double getOdometerEnd() {
        return odometerEnd;
    }

    public void setOdometerEnd(Double odometerEnd) {
        this.odometerEnd = odometerEnd;
    }

    @Column(name = "machine_hours_start")
    public Double getMachineHoursStart() {
        return machineHoursStart;
    }

    public void setMachineHoursStart(Double machineHoursStart) {
        this.machineHoursStart = machineHoursStart;
    }

    @Column(name = "machine_hours_end")
    public Double getMachineHoursEnd() {
        return machineHoursEnd;
    }

    public void setMachineHoursEnd(Double machineHoursEnd) {
        this.machineHoursEnd = machineHoursEnd;
    }

    @Column(name = "engine_hours_start")
    public Double getEngineHoursStart() {
        return engineHoursStart;
    }

    public void setEngineHoursStart(Double engineHoursStart) {
        this.engineHoursStart = engineHoursStart;
    }

    @Column(name = "engine_hours_end")
    public Double getEngineHoursEnd() {
        return engineHoursEnd;
    }

    public void setEngineHoursEnd(Double engineHoursEnd) {
        this.engineHoursEnd = engineHoursEnd;
    }

    @Column(name = "idle_time")
    public Double getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(Double idleTime) {
        this.idleTime = idleTime;
    }

    @Column(name = "engine_warm_up_time")
    public Double getEngineWarmUpTime() {
        return engineWarmUpTime;
    }

    public void setEngineWarmUpTime(Double engineWarmUpTime) {
        this.engineWarmUpTime = engineWarmUpTime;
    }

    @Column(name = "air_conditioner_time")
    public Double getAirConditionerTime() {
        return airConditionerTime;
    }

    public void setAirConditionerTime(Double airConditionerTime) {
        this.airConditionerTime = airConditionerTime;
    }

    @ManyToOne
    @JoinColumn(name = "applied_fuel_norm_id")
    public VehicleFuelNorms getAppliedFuelNorm() {
        return appliedFuelNorm;
    }

    public void setAppliedFuelNorm(VehicleFuelNorms appliedFuelNorm) {
        this.appliedFuelNorm = appliedFuelNorm;
    }

    @Column(name = "calculated_normative_fuel")
    public Double getCalculatedNormativeFuel() {
        return calculatedNormativeFuel;
    }

    public void setCalculatedNormativeFuel(Double calculatedNormativeFuel) {
        this.calculatedNormativeFuel = calculatedNormativeFuel;
    }

    @Column(name = "calculated_actual_fuel")
    public Double getCalculatedActualFuel() {
        return calculatedActualFuel;
    }

    public void setCalculatedActualFuel(Double calculatedActualFuel) {
        this.calculatedActualFuel = calculatedActualFuel;
    }

    @Column(name = "calculated_deviation")
    public Double getCalculatedDeviation() {
        return calculatedDeviation;
    }

    public void setCalculatedDeviation(Double calculatedDeviation) {
        this.calculatedDeviation = calculatedDeviation;
    }

    @Column(name = "closed_at")
    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
}
