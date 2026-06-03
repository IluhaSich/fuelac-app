package com.fuelac.fuelac.service;

import com.fuelac.fuelac.dto.request.VehicleFuelNormsRequest;
import com.fuelac.fuelac.dto.response.VehicleFuelNormsResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.infrastructure.AppUserDetails;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleFuelNormsService {

    VehicleFuelNormsResponse create(VehicleFuelNormsRequest request, AppUserDetails principal);

    Optional<VehicleFuelNormsResponse> findById(UUID id, AppUserDetails principal);

    List<VehicleFuelNormsResponse> findAll(AppUserDetails principal);

    Page<VehicleFuelNormsResponse> findAll(AppUserDetails principal, Pageable pageable);

    Optional<VehicleFuelNormsResponse> findActiveByVehicleAndDate(UUID vehicleId, LocalDate date, AppUserDetails principal);

    Page<VehicleFuelNormsResponse> findByVehicleId(UUID vehicleId, boolean activeOnly, AppUserDetails principal, Pageable pageable);

    VehicleFuelNormsResponse update(UUID id, VehicleFuelNormsRequest request, AppUserDetails principal);

    void deleteById(UUID id, AppUserDetails principal);

    Page<VehicleFuelNormsResponse> search(SearchRequest request, AppUserDetails principal);
}
