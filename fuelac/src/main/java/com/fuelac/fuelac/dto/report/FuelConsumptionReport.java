package com.fuelac.fuelac.dto.report;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FuelConsumptionReport {

    private String vehicleRegistrationNumber;
    private String vehicleGarageNumber;
    private String vehicleBrand;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private List<WaybillReportItem> items;
    private Double totalNormativeFuel;
    private Double totalActualFuel;
    private Double totalDeviation;

    public String getVehicleRegistrationNumber() {
        return vehicleRegistrationNumber;
    }

    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
    }

    public String getVehicleGarageNumber() {
        return vehicleGarageNumber;
    }

    public void setVehicleGarageNumber(String vehicleGarageNumber) {
        this.vehicleGarageNumber = vehicleGarageNumber;
    }

    public String getVehicleBrand() {
        return vehicleBrand;
    }

    public void setVehicleBrand(String vehicleBrand) {
        this.vehicleBrand = vehicleBrand;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public List<WaybillReportItem> getItems() {
        return items;
    }

    public void setItems(List<WaybillReportItem> items) {
        this.items = items;
    }

    public Double getTotalNormativeFuel() {
        return totalNormativeFuel;
    }

    public void setTotalNormativeFuel(Double totalNormativeFuel) {
        this.totalNormativeFuel = totalNormativeFuel;
    }

    public Double getTotalActualFuel() {
        return totalActualFuel;
    }

    public void setTotalActualFuel(Double totalActualFuel) {
        this.totalActualFuel = totalActualFuel;
    }

    public Double getTotalDeviation() {
        return totalDeviation;
    }

    public void setTotalDeviation(Double totalDeviation) {
        this.totalDeviation = totalDeviation;
    }
}
