package com.fuelac.fuelac.service;

import com.fuelac.fuelac.dto.report.FuelConsumptionReport;
import com.fuelac.fuelac.dto.request.WaybillRequest;
import com.fuelac.fuelac.dto.response.WaybillResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.enums.WaybillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaybillService {

    WaybillResponse create(WaybillRequest request, AppUserDetails principal);

    Optional<WaybillResponse> findById(UUID id, AppUserDetails principal);

    List<WaybillResponse> findAll(AppUserDetails principal);

    Page<WaybillResponse> findAll(AppUserDetails principal, Pageable pageable);

    List<WaybillResponse> findByStatus(WaybillStatus status, AppUserDetails principal);

    Page<WaybillResponse> findByStatus(WaybillStatus status, AppUserDetails principal, Pageable pageable);

    List<WaybillResponse> findByVehicleId(UUID vehicleId, AppUserDetails principal);

    Page<WaybillResponse> findByVehicleId(UUID vehicleId, AppUserDetails principal, Pageable pageable);

    List<WaybillResponse> findByDriverId(UUID driverId, AppUserDetails principal);

    Page<WaybillResponse> findByDriverId(UUID driverId, AppUserDetails principal, Pageable pageable);

    List<WaybillResponse> findByDateRange(LocalDateTime from, LocalDateTime to, AppUserDetails principal);

    Page<WaybillResponse> findByDateRange(LocalDateTime from, LocalDateTime to, AppUserDetails principal, Pageable pageable);

    WaybillResponse update(UUID id, WaybillRequest request, AppUserDetails principal);

    void deleteById(UUID id, AppUserDetails principal);

    WaybillResponse closeWaybill(UUID id, AppUserDetails principal);

    FuelConsumptionReport generateReport(UUID vehicleId, LocalDate from, LocalDate to, AppUserDetails principal);

    Page<WaybillResponse> search(SearchRequest request, AppUserDetails principal);
}
