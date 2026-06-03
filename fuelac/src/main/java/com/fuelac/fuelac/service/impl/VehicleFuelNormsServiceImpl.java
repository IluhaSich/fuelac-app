package com.fuelac.fuelac.service.impl;

import com.fuelac.fuelac.dto.request.VehicleFuelNormsRequest;
import com.fuelac.fuelac.dto.response.VehicleFuelNormsResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.entity.Vehicle;
import com.fuelac.fuelac.model.entity.VehicleFuelNorms;
import com.fuelac.fuelac.repository.VehicleFuelNormsRepository;
import com.fuelac.fuelac.repository.VehicleRepository;
import com.fuelac.fuelac.repository.specification.GenericSpecification;
import com.fuelac.fuelac.service.VehicleFuelNormsService;
import com.fuelac.fuelac.service.recalculation.WaybillRecalculationService;
import com.fuelac.fuelac.util.PageableUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VehicleFuelNormsServiceImpl implements VehicleFuelNormsService {

    private final VehicleFuelNormsRepository normsRepository;
    private final VehicleRepository vehicleRepository;
    private final GenericSpecification genericSpecification;
    private final WaybillRecalculationService waybillRecalculationService;

    public VehicleFuelNormsServiceImpl(VehicleFuelNormsRepository normsRepository, VehicleRepository vehicleRepository, GenericSpecification genericSpecification, WaybillRecalculationService waybillRecalculationService) {
        this.normsRepository = normsRepository;
        this.vehicleRepository = vehicleRepository;
        this.genericSpecification = genericSpecification;
        this.waybillRecalculationService = waybillRecalculationService;
    }

    private Organization requireOrganization(AppUserDetails principal) {
        return java.util.Optional.ofNullable(principal.getOrganization())
                .orElseThrow(() -> new IllegalStateException("Организация обязательна для этой операции"));
    }

    private Vehicle resolveVehicle(UUID vehicleId, Organization org) {
        return vehicleRepository.findByIdAndOrganization(vehicleId, org)
                .orElseThrow(() -> new RuntimeException("Транспортное средство не найдено с id: " + vehicleId));
    }

    private void mapRequestToEntity(VehicleFuelNormsRequest request, VehicleFuelNorms norm, Organization org) {
        norm.setVehicle(resolveVehicle(request.getVehicleId(), org));
        norm.setValidFrom(request.getValidFrom());
        norm.setValidTo(request.getValidTo());
        norm.setDescription(request.getDescription());
        norm.setFuelNormPerKm(request.getFuelNormPerKm());
        norm.setFuelNormPerMachineHour(request.getFuelNormPerMachineHour());
        norm.setFuelNormPerEngineHour(request.getFuelNormPerEngineHour());
        norm.setFuelNormIdle(request.getFuelNormIdle());
        norm.setFuelNormWarmUp(request.getFuelNormWarmUp());
        norm.setFuelNormAirConditioner(request.getFuelNormAirConditioner());
    }

    @Override
    public VehicleFuelNormsResponse create(VehicleFuelNormsRequest request, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        VehicleFuelNorms norm = new VehicleFuelNorms();
        norm.setOrganization(org);
        mapRequestToEntity(request, norm, org);
        VehicleFuelNorms saved = normsRepository.save(norm);
        return VehicleFuelNormsResponse.fromEntity(saved);
    }

    @Override
    public Optional<VehicleFuelNormsResponse> findById(UUID id, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return normsRepository.findByIdAndOrganization(id, org)
                .map(VehicleFuelNormsResponse::fromEntity);
    }

    @Override
    public List<VehicleFuelNormsResponse> findAll(AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return normsRepository.findByOrganization(org).stream()
                .map(VehicleFuelNormsResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VehicleFuelNormsResponse> findAll(AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return normsRepository.findByOrganization(org, pageable)
                .map(VehicleFuelNormsResponse::fromEntity);
    }

    @Override
    public Optional<VehicleFuelNormsResponse> findActiveByVehicleAndDate(UUID vehicleId, LocalDate date, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return vehicleRepository.findByIdAndOrganization(vehicleId, org)
                .flatMap(vehicle -> normsRepository.findActiveByVehicleAndDate(org, vehicle, date)
                        .map(VehicleFuelNormsResponse::fromEntity));
    }

    @Override
    public Page<VehicleFuelNormsResponse> findByVehicleId(UUID vehicleId, boolean activeOnly, AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        Vehicle vehicle = vehicleRepository.findByIdAndOrganization(vehicleId, org)
                .orElseThrow(() -> new RuntimeException("Транспортное средство не найдено с id: " + vehicleId));
        if (activeOnly) {
            return normsRepository.findActiveByVehicle(org, vehicle, pageable)
                    .map(VehicleFuelNormsResponse::fromEntity);
        } else {
            return normsRepository.findByOrganizationAndVehicle(org, vehicle, pageable)
                    .map(VehicleFuelNormsResponse::fromEntity);
        }
    }

    @Override
    public VehicleFuelNormsResponse update(UUID id, VehicleFuelNormsRequest request, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        VehicleFuelNorms existing = normsRepository.findByIdAndOrganization(id, org)
                .orElseThrow(() -> new RuntimeException("Норма расхода топлива не найдена с id: " + id));
        mapRequestToEntity(request, existing, org);
        VehicleFuelNorms saved = normsRepository.save(existing);
        waybillRecalculationService.recalculateWaybillsForNorm(saved);
        return VehicleFuelNormsResponse.fromEntity(saved);
    }

    @Override
    public void deleteById(UUID id, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        normsRepository.deleteByIdAndOrganization(id, org);
    }

    @Override
    public Page<VehicleFuelNormsResponse> search(SearchRequest request, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        Specification<VehicleFuelNorms> orgSpec = (root, query, cb) -> cb.equal(root.get("organization"), org);
        Specification<VehicleFuelNorms> filterSpec = genericSpecification.getSpecification(request.getFilters());
        Pageable pageable = PageableUtil.fromSearchRequest(request);
        return normsRepository.findAll(Specification.where(orgSpec).and(filterSpec), pageable)
                .map(VehicleFuelNormsResponse::fromEntity);
    }
}
