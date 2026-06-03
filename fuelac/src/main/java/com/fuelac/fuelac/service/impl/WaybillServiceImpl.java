package com.fuelac.fuelac.service.impl;

import com.fuelac.fuelac.dto.report.FuelConsumptionReport;
import com.fuelac.fuelac.dto.report.WaybillReportItem;
import com.fuelac.fuelac.dto.request.WaybillRequest;
import com.fuelac.fuelac.dto.response.WaybillResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.entity.*;
import com.fuelac.fuelac.model.enums.WaybillStatus;
import com.fuelac.fuelac.repository.*;
import com.fuelac.fuelac.repository.specification.GenericSpecification;
import com.fuelac.fuelac.service.calculator.FuelCalculationService;
import com.fuelac.fuelac.service.WaybillService;
import com.fuelac.fuelac.util.PageableUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WaybillServiceImpl implements WaybillService {

    private final WaybillRepository waybillRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final VehicleFuelNormsRepository normsRepository;
    private final UserRepository userRepository;
    private final FuelCalculationService fuelCalculationService;
    private final GenericSpecification genericSpecification;

    public WaybillServiceImpl(WaybillRepository waybillRepository, VehicleRepository vehicleRepository, DriverRepository driverRepository, VehicleFuelNormsRepository normsRepository, UserRepository userRepository, FuelCalculationService fuelCalculationService, GenericSpecification genericSpecification) {
        this.waybillRepository = waybillRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.normsRepository = normsRepository;
        this.userRepository = userRepository;
        this.fuelCalculationService = fuelCalculationService;
        this.genericSpecification = genericSpecification;
    }

    private Organization requireOrganization(AppUserDetails principal) {
        Organization org = principal.getOrganization();
        if (org == null) {
            throw new IllegalStateException("Организация обязательна для данной операции");
        }
        return org;
    }

    private User resolveUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с id: " + userId));
    }

    private Driver resolveDriver(UUID driverId, Organization org) {
        return driverRepository.findByIdAndOrganization(driverId, org)
                .orElseThrow(() -> new RuntimeException("Водитель не найден с id: " + driverId));
    }

    private Vehicle resolveVehicle(UUID vehicleId, Organization org) {
        return vehicleRepository.findByIdAndOrganization(vehicleId, org)
                .orElseThrow(() -> new RuntimeException("Транспортное средство не найдено с id: " + vehicleId));
    }

    private VehicleFuelNorms resolveNorm(UUID normId, Organization org) {
        return normsRepository.findByIdAndOrganization(normId, org)
                .orElseThrow(() -> new RuntimeException("Норма расхода топлива не найдена с id: " + normId));
    }

    private void mapRequestToEntity(WaybillRequest request, Waybill waybill, Organization org) {
        waybill.setNumber(request.getNumber());
        waybill.setStatus(request.getStatus());
        waybill.setVehicleType(request.getVehicleType());
        waybill.setMessageType(request.getMessageType());
        waybill.setTransportationType(request.getTransportationType());
        if (request.getDriverId() != null) {
            waybill.setDriver(resolveDriver(request.getDriverId(), org));
        }
        if (request.getVehicleId() != null) {
            waybill.setVehicle(resolveVehicle(request.getVehicleId(), org));
        }
        waybill.setWorkStartDate(request.getWorkStartDate());
        waybill.setWorkEndDate(request.getWorkEndDate());
        waybill.setFuelStart(request.getFuelStart());
        waybill.setFuelEnd(request.getFuelEnd());
        waybill.setFuelRefilled(request.getFuelRefilled());
        waybill.setOdometerStart(request.getOdometerStart());
        waybill.setOdometerEnd(request.getOdometerEnd());
        waybill.setMachineHoursStart(request.getMachineHoursStart());
        waybill.setMachineHoursEnd(request.getMachineHoursEnd());
        waybill.setEngineHoursStart(request.getEngineHoursStart());
        waybill.setEngineHoursEnd(request.getEngineHoursEnd());
        waybill.setIdleTime(request.getIdleTime());
        waybill.setEngineWarmUpTime(request.getEngineWarmUpTime());
        waybill.setAirConditionerTime(request.getAirConditionerTime());
        if (request.getAppliedFuelNormId() != null) {
            waybill.setAppliedFuelNorm(resolveNorm(request.getAppliedFuelNormId(), org));
        }
        waybill.setCalculatedNormativeFuel(request.getCalculatedNormativeFuel());
        waybill.setCalculatedActualFuel(request.getCalculatedActualFuel());
        waybill.setCalculatedDeviation(request.getCalculatedDeviation());
        waybill.setClosedAt(request.getClosedAt());
    }

    @Override
    public WaybillResponse create(WaybillRequest request, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        Waybill waybill = new Waybill();
        waybill.setOrganization(org);
        waybill.setCreatedBy(resolveUser(principal.getId()));
        mapRequestToEntity(request, waybill, org);
        waybill.setStatus(WaybillStatus.OPEN);
        Waybill saved = waybillRepository.save(waybill);
        return WaybillResponse.fromEntity(saved);
    }

    @Override
    public Optional<WaybillResponse> findById(UUID id, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return waybillRepository.findByIdAndOrganization(id, org)
                .map(WaybillResponse::fromEntity);
    }

    @Override
    public List<WaybillResponse> findAll(AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return waybillRepository.findByOrganization(org).stream()
                .map(WaybillResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<WaybillResponse> findAll(AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return waybillRepository.findByOrganization(org, pageable)
                .map(WaybillResponse::fromEntity);
    }

    @Override
    public List<WaybillResponse> findByStatus(WaybillStatus status, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return waybillRepository.findByOrganizationAndStatus(org, status).stream()
                .map(WaybillResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<WaybillResponse> findByStatus(WaybillStatus status, AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return waybillRepository.findByOrganizationAndStatus(org, status, pageable)
                .map(WaybillResponse::fromEntity);
    }

    @Override
    public List<WaybillResponse> findByVehicleId(UUID vehicleId, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.findByIdAndOrganization(vehicleId, org)
                .map(vehicle -> waybillRepository.findByOrganizationAndVehicle(org, vehicle).stream()
                        .map(WaybillResponse::fromEntity)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    @Override
    public Page<WaybillResponse> findByVehicleId(UUID vehicleId, AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.findByIdAndOrganization(vehicleId, org)
                .map(vehicle -> waybillRepository.findByOrganizationAndVehicle(org, vehicle, pageable)
                        .map(WaybillResponse::fromEntity))
                .orElse(Page.empty());
    }

    @Override
    public List<WaybillResponse> findByDriverId(UUID driverId, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return driverRepository.findByIdAndOrganization(driverId, org)
                .map(driver -> waybillRepository.findByOrganizationAndDriver(org, driver).stream()
                        .map(WaybillResponse::fromEntity)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    @Override
    public Page<WaybillResponse> findByDriverId(UUID driverId, AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return driverRepository.findByIdAndOrganization(driverId, org)
                .map(driver -> waybillRepository.findByOrganizationAndDriver(org, driver, pageable)
                        .map(WaybillResponse::fromEntity))
                .orElse(Page.empty());
    }

    @Override
    public List<WaybillResponse> findByDateRange(LocalDateTime from, LocalDateTime to, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return waybillRepository.findByDateRange(org, from, to).stream()
                .map(WaybillResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<WaybillResponse> findByDateRange(LocalDateTime from, LocalDateTime to, AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return waybillRepository.findByDateRange(org, from, to, pageable)
                .map(WaybillResponse::fromEntity);
    }

    @Override
    public WaybillResponse update(UUID id, WaybillRequest request, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        Waybill existing = waybillRepository.findByIdAndOrganization(id, org)
                .orElseThrow(() -> new RuntimeException("Путевой лист не найден с id: " + id));
        if (existing.getStatus() == WaybillStatus.CLOSED) {
            if (!principal.isOrganizationManager()) {
                throw new IllegalStateException("Редактировать закрытый путевой лист может только менеджер");
            }
        }
        WaybillStatus originalStatus = existing.getStatus();
        mapRequestToEntity(request, existing, org);
        existing.setStatus(originalStatus); // статус через update не меняем

        // Если путевой лист уже закрыт — пересчитываем расход по актуальным данным и норме.
        // (Для открытых ПЛ расчёт будет сделан при закрытии.)
        if (originalStatus == WaybillStatus.CLOSED) {
            fuelCalculationService.calculateFuelConsumption(existing);
        }

        return WaybillResponse.fromEntity(waybillRepository.save(existing));
    }

    @Override
    public void deleteById(UUID id, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        Waybill existing = waybillRepository.findByIdAndOrganization(id, org)
                .orElseThrow(() -> new RuntimeException("Путевой лист не найден с id: " + id));
        if (existing.getStatus() == WaybillStatus.CLOSED) {
            throw new IllegalStateException("Нельзя удалить закрытый путевой лист");
        }
        waybillRepository.deleteByIdAndOrganization(id, org);
    }

    @Override
    public WaybillResponse closeWaybill(UUID id, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        Waybill waybill = waybillRepository.findByIdAndOrganization(id, org)
                .orElseThrow(() -> new RuntimeException("Путевой лист не найден с id: " + id));

        if (waybill.getStatus() == WaybillStatus.CLOSED) {
            throw new IllegalStateException("Путевой лист уже закрыт");
        }

        if (waybill.getWorkStartDate() == null) {
            throw new IllegalStateException("Для закрытия путевого листа необходимо указать дату выезда");
        }
        if (waybill.getWorkEndDate() == null) {
            throw new IllegalStateException("Для закрытия путевого листа необходимо указать дату возвращения");
        }
        if (!waybill.getWorkEndDate().isAfter(waybill.getWorkStartDate())) {
            throw new IllegalArgumentException("Дата окончания должна быть позже даты начала");
        }
        if (waybill.getOdometerEnd() != null && waybill.getOdometerStart() != null && waybill.getOdometerEnd() <= waybill.getOdometerStart()) {
            throw new IllegalArgumentException("Показание одометра на конце должно быть больше, чем на начале");
        }
        if (waybill.getMachineHoursEnd() != null && waybill.getMachineHoursStart() != null && waybill.getMachineHoursEnd() <= waybill.getMachineHoursStart()) {
            throw new IllegalArgumentException("Машиночасы на конце должны быть больше, чем на начале");
        }
        if (waybill.getEngineHoursEnd() != null && waybill.getEngineHoursStart() != null && waybill.getEngineHoursEnd() <= waybill.getEngineHoursStart()) {
            throw new IllegalArgumentException("Моточасы на конце должны быть больше, чем на начале");
        }
        if (waybill.getFuelEnd() != null && waybill.getFuelStart() != null && waybill.getFuelRefilled() != null) {
            double maxFuel = waybill.getFuelStart() + waybill.getFuelRefilled();
            if (waybill.getFuelEnd() > maxFuel) {
                throw new IllegalArgumentException("Остаток топлива на конце не может превышать начальный остаток плюс заправку");
            }
        }

        fuelCalculationService.calculateFuelConsumption(waybill);

        waybill.setStatus(WaybillStatus.CLOSED);
        waybill.setClosedAt(LocalDateTime.now());
        waybill.setClosedBy(resolveUser(principal.getId()));

        return WaybillResponse.fromEntity(waybillRepository.save(waybill));
    }

    @Override
    public FuelConsumptionReport generateReport(UUID vehicleId, LocalDate from, LocalDate to, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        Vehicle vehicle = vehicleRepository.findByIdAndOrganization(vehicleId, org)
                .orElseThrow(() -> new RuntimeException("Транспортное средство не найдено с id: " + vehicleId));

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();

        var waybills = waybillRepository.findByVehicleAndDateRange(
                org, vehicle, fromDateTime, toDateTime);

        FuelConsumptionReport report = new FuelConsumptionReport();
        report.setVehicleRegistrationNumber(vehicle.getRegistrationNumber());
        report.setVehicleGarageNumber(vehicle.getGarageNumber());
        report.setVehicleBrand(vehicle.getBrand() + " " + vehicle.getModel());
        report.setPeriodStart(from);
        report.setPeriodEnd(to);

        double totalNormative = 0.0;
        double totalActual = 0.0;
        double totalDeviation = 0.0;

        var items = waybills.stream().map(waybill -> {
            WaybillReportItem item = new WaybillReportItem();
            item.setWaybillNumber(waybill.getNumber());
            item.setDriverFullName(waybill.getDriver().getFullName());
            item.setWorkStartDate(waybill.getWorkStartDate());
            item.setWorkEndDate(waybill.getWorkEndDate());
            item.setFuelStart(waybill.getFuelStart());
            item.setFuelEnd(waybill.getFuelEnd());
            item.setFuelRefilled(waybill.getFuelRefilled());
            item.setOdometerStart(waybill.getOdometerStart());
            item.setOdometerEnd(waybill.getOdometerEnd());
            item.setMachineHoursStart(waybill.getMachineHoursStart());
            item.setMachineHoursEnd(waybill.getMachineHoursEnd());
            item.setEngineHoursStart(waybill.getEngineHoursStart());
            item.setEngineHoursEnd(waybill.getEngineHoursEnd());
            item.setIdleTime(waybill.getIdleTime());
            item.setEngineWarmUpTime(waybill.getEngineWarmUpTime());
            item.setAirConditionerTime(waybill.getAirConditionerTime());

            VehicleFuelNorms norm = waybill.getAppliedFuelNorm();
            if (norm != null) {
                item.setFuelNormPer100Km(norm.getFuelNormPerKm());
                item.setFuelNormPerMachineHour(norm.getFuelNormPerMachineHour());
                item.setFuelNormPerEngineHour(norm.getFuelNormPerEngineHour());
                item.setFuelNormIdle(norm.getFuelNormIdle());
                item.setFuelNormWarmUp(norm.getFuelNormWarmUp());
                item.setFuelNormAirConditioner(norm.getFuelNormAirConditioner());
            }

            double mileage = waybill.getOdometerEnd() != null && waybill.getOdometerStart() != null
                    ? waybill.getOdometerEnd() - waybill.getOdometerStart() : 0.0;
            double machineHours = waybill.getMachineHoursEnd() != null && waybill.getMachineHoursStart() != null
                    ? waybill.getMachineHoursEnd() - waybill.getMachineHoursStart() : 0.0;
            double engineHours = waybill.getEngineHoursEnd() != null && waybill.getEngineHoursStart() != null
                    ? waybill.getEngineHoursEnd() - waybill.getEngineHoursStart() : 0.0;

            item.setMileageKm(mileage);
            item.setMachineHours(machineHours);
            item.setEngineHours(engineHours);

            double hoursWorked = waybill.getWorkEndDate() != null && waybill.getWorkStartDate() != null
                    ? java.time.Duration.between(waybill.getWorkStartDate(), waybill.getWorkEndDate()).toMinutes() / 60.0
                    : 0.0;
            item.setHoursWorked(hoursWorked);

            double normative = waybill.getCalculatedNormativeFuel() != null ? waybill.getCalculatedNormativeFuel() : 0.0;
            double actual = waybill.getCalculatedActualFuel() != null ? waybill.getCalculatedActualFuel() : 0.0;
            double deviation = waybill.getCalculatedDeviation() != null ? waybill.getCalculatedDeviation() : 0.0;

            item.setNormativeFuel(normative);
            item.setActualFuel(actual);
            item.setDeviation(deviation);

            return item;
        }).collect(Collectors.toList());

        for (WaybillReportItem item : items) {
            totalNormative += item.getNormativeFuel();
            totalActual += item.getActualFuel();
            totalDeviation += item.getDeviation();
        }

        report.setItems(items);
        report.setTotalNormativeFuel(totalNormative);
        report.setTotalActualFuel(totalActual);
        report.setTotalDeviation(totalDeviation);

        return report;
    }

    @Override
    public Page<WaybillResponse> search(SearchRequest request, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        Specification<Waybill> orgSpec = (root, query, cb) -> cb.equal(root.get("organization"), org);
        Specification<Waybill> filterSpec = genericSpecification.getSpecification(request.getFilters());
        // Если клиент не задал сортировку — показываем OPEN первыми, затем по дате DESC.
        // "OPEN" > "CLOSED" лексикографически, поэтому DESC по status даёт OPEN первыми.
        Pageable pageable;
        if (request.getSorts() == null || request.getSorts().isEmpty()) {
            int page = request.getPage() != null ? request.getPage() : 0;
            int size = request.getSize() != null ? request.getSize() : 20;
            pageable = PageRequest.of(page, size,
                    Sort.by(Sort.Direction.DESC, "status")
                        .and(Sort.by(Sort.Direction.DESC, "workStartDate")));
        } else {
            pageable = PageableUtil.fromSearchRequest(request);
        }
        return waybillRepository.findAll(Specification.where(orgSpec).and(filterSpec), pageable)
                .map(WaybillResponse::fromEntity);
    }
}
