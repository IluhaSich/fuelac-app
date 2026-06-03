package com.fuelac.fuelac.service.impl;

import com.fuelac.fuelac.dto.request.VehicleRequest;
import com.fuelac.fuelac.dto.response.VehicleResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.dto.utility.VehicleReadings;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.entity.Vehicle;
import com.fuelac.fuelac.model.entity.Waybill;
import com.fuelac.fuelac.model.enums.VehicleType;
import com.fuelac.fuelac.repository.VehicleRepository;
import com.fuelac.fuelac.repository.WaybillRepository;
import com.fuelac.fuelac.repository.specification.GenericSpecification;
import com.fuelac.fuelac.service.VehicleService;
import com.fuelac.fuelac.util.PageableUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final WaybillRepository waybillRepository;
    private final GenericSpecification genericSpecification;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, WaybillRepository waybillRepository, GenericSpecification genericSpecification) {
        this.vehicleRepository = vehicleRepository;
        this.waybillRepository = waybillRepository;
        this.genericSpecification = genericSpecification;
    }

    private Organization requireOrganization(AppUserDetails principal) {
        Organization org = principal.getOrganization();
        if (org == null) {
            throw new IllegalStateException("Организация обязательна для данной операции");
        }
        return org;
    }

    @Override
    public VehicleResponse create(VehicleRequest request, AppUserDetails principal) {
        Vehicle vehicle = request.toEntity();
        vehicle.setOrganization(requireOrganization(principal));
        Vehicle saved = vehicleRepository.save(vehicle);
        return VehicleResponse.fromEntity(saved);
    }

    @Override
    public Optional<VehicleResponse> findById(UUID id, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.findByIdAndOrganization(id, org)
                .map(VehicleResponse::fromEntity);
    }

    @Override
    public List<VehicleResponse> findAll(AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.findByOrganization(org).stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VehicleResponse> findAll(AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.findByOrganization(org, pageable)
                .map(VehicleResponse::fromEntity);
    }

    @Override
    public List<VehicleResponse> findByType(VehicleType type, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.findByOrganizationAndType(org, type).stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VehicleResponse> findByType(VehicleType type, AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.findByOrganizationAndType(org, type, pageable)
                .map(VehicleResponse::fromEntity);
    }

    @Override
    public VehicleResponse update(UUID id, VehicleRequest request, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        Vehicle existing = vehicleRepository.findByIdAndOrganization(id, org)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));
        existing.setType(request.getType());
        existing.setBrand(request.getBrand());
        existing.setModel(request.getModel());
        existing.setYear(request.getYear());
        existing.setRegistrationNumber(request.getRegistrationNumber());
        existing.setGarageNumber(request.getGarageNumber());
        return VehicleResponse.fromEntity(vehicleRepository.save(existing));
    }

    @Override
    public void deleteById(UUID id, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        vehicleRepository.deleteByIdAndOrganization(id, org);
    }

    @Override
    public Optional<VehicleResponse> findByRegistrationNumber(String registrationNumber, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.findByRegistrationNumberAndOrganization(registrationNumber, org)
                .map(VehicleResponse::fromEntity);
    }

    @Override
    public Optional<VehicleResponse> findByGarageNumber(String garageNumber, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.findByGarageNumberAndOrganization(garageNumber, org)
                .map(VehicleResponse::fromEntity);
    }

    @Override
    public List<VehicleResponse> findByBrandAndModel(String brand, String model, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.findByBrandAndModel(org, brand, model).stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VehicleResponse> findByBrandAndModel(String brand, String model, AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.findByBrandAndModel(org, brand, model, pageable)
                .map(VehicleResponse::fromEntity);
    }

    @Override
    public List<VehicleResponse> findByBrandAndModelAndYear(String brand, String model, Integer year, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.findByBrandAndModelAndYear(org, brand, model, year).stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VehicleResponse> searchByRegistrationNumber(String registrationNumber, AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.searchByRegistrationNumber(org, registrationNumber, pageable)
                .map(VehicleResponse::fromEntity);
    }

    @Override
    public Page<VehicleResponse> searchByGarageNumber(String garageNumber, AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.searchByGarageNumber(org, garageNumber, pageable)
                .map(VehicleResponse::fromEntity);
    }

    @Override
    public Page<VehicleResponse> searchByBrand(String brand, AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.searchByBrand(org, brand, pageable)
                .map(VehicleResponse::fromEntity);
    }

    @Override
    public Optional<VehicleReadings> getLastWaybillReadings(UUID vehicleId, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        Vehicle vehicle = vehicleRepository.findByIdAndOrganization(vehicleId, org)
                .orElseThrow(() -> new RuntimeException("Транспортное средство не найдено с id: " + vehicleId));
        return waybillRepository.findLastClosedByVehicle(org, vehicle)
                .map(w -> new VehicleReadings(w.getOdometerEnd(), w.getFuelEnd()));
    }

    @Override
    public Optional<VehicleType> getVehicleTypeById(UUID id, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.findByIdAndOrganization(id, org)
                .map(Vehicle::getType);
    }

    @Override
    public Page<VehicleResponse> search(SearchRequest request, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        Specification<Vehicle> orgSpec = (root, query, cb) -> cb.equal(root.get("organization"), org);
        Specification<Vehicle> filterSpec = genericSpecification.getSpecification(request.getFilters());
        Pageable pageable = PageableUtil.fromSearchRequest(request);
        return vehicleRepository.findAll(Specification.where(orgSpec).and(filterSpec), pageable)
                .map(VehicleResponse::fromEntity);
    }
}
