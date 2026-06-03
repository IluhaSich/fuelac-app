package com.fuelac.fuelac.service.recalculation;

import com.fuelac.fuelac.model.entity.VehicleFuelNorms;
import com.fuelac.fuelac.model.entity.Waybill;
import com.fuelac.fuelac.model.enums.WaybillStatus;
import com.fuelac.fuelac.repository.WaybillRepository;
import com.fuelac.fuelac.service.calculator.FuelCalculationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WaybillRecalculationService {

    private final WaybillRepository waybillRepository;
    private final FuelCalculationService fuelCalculationService;

    public WaybillRecalculationService(WaybillRepository waybillRepository, FuelCalculationService fuelCalculationService) {
        this.waybillRepository = waybillRepository;
        this.fuelCalculationService = fuelCalculationService;
    }

    @Async
    public void recalculateWaybillsForNorm(VehicleFuelNorms norm) {
        List<Waybill> waybills = waybillRepository.findByAppliedFuelNorm(norm)
                .stream()
                .filter(w -> w.getStatus() == WaybillStatus.CLOSED)
                .collect(Collectors.toList());

        for (Waybill waybill : waybills) {
            fuelCalculationService.calculateFuelConsumption(waybill);
            waybillRepository.save(waybill);
        }
    }
}
