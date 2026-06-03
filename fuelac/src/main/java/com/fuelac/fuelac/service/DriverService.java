package com.fuelac.fuelac.service;

import com.fuelac.fuelac.dto.request.DriverRequest;
import com.fuelac.fuelac.dto.response.DriverResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DriverService {

    DriverResponse create(DriverRequest request, AppUserDetails principal);

    Optional<DriverResponse> findById(UUID id, AppUserDetails principal);

    List<DriverResponse> findAll(AppUserDetails principal);

    Page<DriverResponse> findAll(AppUserDetails principal, Pageable pageable);

    DriverResponse update(UUID id, DriverRequest request, AppUserDetails principal);

    void deleteById(UUID id, AppUserDetails principal);

    Optional<DriverResponse> findByPersonnelNumber(String personnelNumber, AppUserDetails principal);

    List<DriverResponse> searchByName(String name, AppUserDetails principal);

    Page<DriverResponse> searchByName(String name, AppUserDetails principal, Pageable pageable);

    Page<DriverResponse> search(SearchRequest request, AppUserDetails principal);
}
