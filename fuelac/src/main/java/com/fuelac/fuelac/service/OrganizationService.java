package com.fuelac.fuelac.service;

import com.fuelac.fuelac.dto.request.OrganizationRequest;
import com.fuelac.fuelac.dto.response.OrganizationResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationService {

    OrganizationResponse create(OrganizationRequest request);

    Optional<OrganizationResponse> findById(UUID id);

    List<OrganizationResponse> findAll();

    Page<OrganizationResponse> findAll(Pageable pageable);

    OrganizationResponse update(UUID id, OrganizationRequest request);

    void deleteById(UUID id);

    List<OrganizationResponse> searchByName(String name);

    Page<OrganizationResponse> searchByName(String name, Pageable pageable);

    Page<OrganizationResponse> search(SearchRequest request);
}
