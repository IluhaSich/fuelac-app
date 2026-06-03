package com.fuelac.fuelac.service.impl;

import com.fuelac.fuelac.dto.request.DriverRequest;
import com.fuelac.fuelac.dto.response.DriverResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.entity.Driver;
import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.repository.DriverRepository;
import com.fuelac.fuelac.repository.specification.GenericSpecification;
import com.fuelac.fuelac.service.DriverService;
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
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final GenericSpecification genericSpecification;

    public DriverServiceImpl(DriverRepository driverRepository, GenericSpecification genericSpecification) {
        this.driverRepository = driverRepository;
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
    public DriverResponse create(DriverRequest request, AppUserDetails principal) {
        Driver driver = request.toEntity();
        driver.setOrganization(requireOrganization(principal));
        Driver saved = driverRepository.save(driver);
        return DriverResponse.fromEntity(saved);
    }

    @Override
    public Optional<DriverResponse> findById(UUID id, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return driverRepository.findByIdAndOrganization(id, org)
                .map(DriverResponse::fromEntity);
    }

    @Override
    public List<DriverResponse> findAll(AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return driverRepository.findByOrganization(org).stream()
                .map(DriverResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<DriverResponse> findAll(AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return driverRepository.findByOrganization(org, pageable)
                .map(DriverResponse::fromEntity);
    }

    @Override
    public DriverResponse update(UUID id, DriverRequest request, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        Driver existing = driverRepository.findByIdAndOrganization(id, org)
                .orElseThrow(() -> new RuntimeException("Водитель не найден с id: " + id));
        if (!existing.getPersonnelNumber().equals(request.getPersonnelNumber()) &&
            driverRepository.findByPersonnelNumberAndOrganization(request.getPersonnelNumber(), org).isPresent()) {
            throw new IllegalArgumentException("Водитель с табельным номером " + request.getPersonnelNumber() + " уже существует");
        }
        existing.setPersonnelNumber(request.getPersonnelNumber());
        existing.setLastName(request.getLastName());
        existing.setFirstName(request.getFirstName());
        existing.setPatronymic(request.getPatronymic());
        existing.setSnils(request.getSnils());
        existing.setLicense(request.toEntity().getLicense());
        Driver saved = driverRepository.save(existing);
        return DriverResponse.fromEntity(saved);
    }

    @Override
    public void deleteById(UUID id, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        driverRepository.deleteByIdAndOrganization(id, org);
    }

    @Override
    public Optional<DriverResponse> findByPersonnelNumber(String personnelNumber, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return driverRepository.findByPersonnelNumberAndOrganization(personnelNumber, org)
                .map(DriverResponse::fromEntity);
    }

    @Override
    public List<DriverResponse> searchByName(String name, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return driverRepository.searchByName(org, name).stream()
                .map(DriverResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<DriverResponse> searchByName(String name, AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return driverRepository.searchByName(org, name, pageable)
                .map(DriverResponse::fromEntity);
    }

    @Override
    public Page<DriverResponse> search(SearchRequest request, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        Specification<Driver> orgSpec = (root, query, cb) -> cb.equal(root.get("organization"), org);
        Specification<Driver> filterSpec = genericSpecification.getSpecification(request.getFilters());
        Pageable pageable = PageableUtil.fromSearchRequest(request);
        return driverRepository.findAll(Specification.where(orgSpec).and(filterSpec), pageable)
                .map(DriverResponse::fromEntity);
    }
}
