package com.fuelac.fuelac.service.impl;

import com.fuelac.fuelac.dto.request.OrganizationRequest;
import com.fuelac.fuelac.dto.response.OrganizationResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.repository.OrganizationRepository;
import com.fuelac.fuelac.repository.specification.GenericSpecification;
import com.fuelac.fuelac.service.OrganizationService;
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
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final GenericSpecification genericSpecification;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository, GenericSpecification genericSpecification) {
        this.organizationRepository = organizationRepository;
        this.genericSpecification = genericSpecification;
    }

    @Override
    public OrganizationResponse create(OrganizationRequest request) {
        Organization org = request.toEntity();
        Organization saved = organizationRepository.save(org);
        return OrganizationResponse.fromEntity(saved);
    }

    @Override
    public Optional<OrganizationResponse> findById(UUID id) {
        return organizationRepository.findById(id)
                .map(OrganizationResponse::fromEntity);
    }

    @Override
    public List<OrganizationResponse> findAll() {
        return organizationRepository.findAll().stream()
                .map(OrganizationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<OrganizationResponse> findAll(Pageable pageable) {
        return organizationRepository.findAll(pageable)
                .map(OrganizationResponse::fromEntity);
    }

    @Override
    public OrganizationResponse update(UUID id, OrganizationRequest request) {
        Organization existing = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + id));
        existing.setCodeOKUD(request.getCodeOKUD());
        existing.setCodeOKPO(request.getCodeOKPO());
        existing.setOgrn(request.getOgrn());
        existing.setName(request.getName());
        existing.setAddress(request.getAddress());
        Organization saved = organizationRepository.save(existing);
        return OrganizationResponse.fromEntity(saved);
    }

    @Override
    public void deleteById(UUID id) {
        organizationRepository.deleteById(id);
    }

    @Override
    public List<OrganizationResponse> searchByName(String name) {
        return organizationRepository.findByNameContainingIgnoreCase(name).stream()
                .map(OrganizationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<OrganizationResponse> searchByName(String name, Pageable pageable) {
        return organizationRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(OrganizationResponse::fromEntity);
    }

    @Override
    public Page<OrganizationResponse> search(SearchRequest request) {
        Specification<Organization> spec = genericSpecification.getSpecification(request.getFilters());
        Pageable pageable = PageableUtil.fromSearchRequest(request);
        return organizationRepository.findAll(spec, pageable)
                .map(OrganizationResponse::fromEntity);
    }
}
