package com.fuelac.fuelac.service.calculator;

import com.fuelac.fuelac.model.entity.VehicleFuelNorms;
import com.fuelac.fuelac.model.entity.Waybill;
import org.springframework.stereotype.Service;

@Service
public class FuelCalculationService {

    public void calculateFuelConsumption(Waybill waybill) {
        VehicleFuelNorms norm = waybill.getAppliedFuelNorm();
        if (norm == null) {
            throw new IllegalStateException("Применяемая норма расхода топлива обязательна для расчета");
        }

        double normativeFuel = calculateNormativeFuel(waybill, norm);
        double actualFuel = calculateActualFuel(waybill);
        double deviation = actualFuel - normativeFuel;

        waybill.setCalculatedNormativeFuel(normativeFuel);
        waybill.setCalculatedActualFuel(actualFuel);
        waybill.setCalculatedDeviation(deviation);
    }

    private double calculateNormativeFuel(Waybill waybill, VehicleFuelNorms norm) {
        double normativeFuel = 0.0;

        // Расход на пробег (л/100км -> л/км)
        if (waybill.getOdometerStart() != null && waybill.getOdometerEnd() != null
                && norm.getFuelNormPerKm() != null) {
            double mileage = waybill.getOdometerEnd() - waybill.getOdometerStart();
            if (mileage > 0) {
                normativeFuel += (mileage / 100.0) * norm.getFuelNormPerKm();
            }
        }

        // Расход машиночасов
        if (waybill.getMachineHoursStart() != null && waybill.getMachineHoursEnd() != null
                && norm.getFuelNormPerMachineHour() != null) {
            double machineHours = waybill.getMachineHoursEnd() - waybill.getMachineHoursStart();
            if (machineHours > 0) {
                normativeFuel += machineHours * norm.getFuelNormPerMachineHour();
            }
        }

        // Расход моточасов
        if (waybill.getEngineHoursStart() != null && waybill.getEngineHoursEnd() != null
                && norm.getFuelNormPerEngineHour() != null) {
            double engineHours = waybill.getEngineHoursEnd() - waybill.getEngineHoursStart();
            if (engineHours > 0) {
                normativeFuel += engineHours * norm.getFuelNormPerEngineHour();
            }
        }

        // Расход холостой ход
        if (waybill.getIdleTime() != null && norm.getFuelNormIdle() != null) {
            normativeFuel += waybill.getIdleTime() * norm.getFuelNormIdle();
        }

        // Расход прогрев
        if (waybill.getEngineWarmUpTime() != null && norm.getFuelNormWarmUp() != null) {
            normativeFuel += waybill.getEngineWarmUpTime() * norm.getFuelNormWarmUp();
        }

        // Расход кондиционер
        if (waybill.getAirConditionerTime() != null && norm.getFuelNormAirConditioner() != null) {
            normativeFuel += waybill.getAirConditionerTime() * norm.getFuelNormAirConditioner();
        }

        return round(normativeFuel);
    }

    private double calculateActualFuel(Waybill waybill) {
        double fuelStart = waybill.getFuelStart() != null ? waybill.getFuelStart() : 0.0;
        double fuelRefilled = waybill.getFuelRefilled() != null ? waybill.getFuelRefilled() : 0.0;
        double fuelEnd = waybill.getFuelEnd() != null ? waybill.getFuelEnd() : 0.0;
        return round(fuelStart + fuelRefilled - fuelEnd);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
