package com.fuelac.fuelac.service;

import com.fuelac.fuelac.dto.request.VehicleRequest;
import com.fuelac.fuelac.dto.response.VehicleResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.dto.utility.VehicleReadings;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.enums.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleService {

    VehicleResponse create(VehicleRequest request, AppUserDetails principal);

    Optional<VehicleResponse> findById(UUID id, AppUserDetails principal);

    List<VehicleResponse> findAll(AppUserDetails principal);

    Page<VehicleResponse> findAll(AppUserDetails principal, Pageable pageable);

    List<VehicleResponse> findByType(VehicleType type, AppUserDetails principal);

    Page<VehicleResponse> findByType(VehicleType type, AppUserDetails principal, Pageable pageable);

    VehicleResponse update(UUID id, VehicleRequest request, AppUserDetails principal);

    void deleteById(UUID id, AppUserDetails principal);

    Optional<VehicleResponse> findByRegistrationNumber(String registrationNumber, AppUserDetails principal);

    Optional<VehicleResponse> findByGarageNumber(String garageNumber, AppUserDetails principal);

    List<VehicleResponse> findByBrandAndModel(String brand, String model, AppUserDetails principal);

    Page<VehicleResponse> findByBrandAndModel(String brand, String model, AppUserDetails principal, Pageable pageable);

    List<VehicleResponse> findByBrandAndModelAndYear(String brand, String model, Integer year, AppUserDetails principal);

    Page<VehicleResponse> searchByRegistrationNumber(String registrationNumber, AppUserDetails principal, Pageable pageable);

    Page<VehicleResponse> searchByGarageNumber(String garageNumber, AppUserDetails principal, Pageable pageable);

    Page<VehicleResponse> searchByBrand(String brand, AppUserDetails principal, Pageable pageable);

    Optional<VehicleReadings> getLastWaybillReadings(UUID vehicleId, AppUserDetails principal);

    Optional<VehicleType> getVehicleTypeById(UUID id, AppUserDetails principal);

    Page<VehicleResponse> search(SearchRequest request, AppUserDetails principal);
}
