package com.fuelac.fuelac.repository;

import com.fuelac.fuelac.model.entity.Organization;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends Repository<Organization, UUID>, JpaSpecificationExecutor<Organization> {

    Organization save(Organization organization);

    Optional<Organization> findById(UUID id);

    List<Organization> findAll();

    Page<Organization> findAll(Pageable pageable);

    void deleteById(UUID id);

    boolean existsById(UUID id);

    List<Organization> findByNameContainingIgnoreCase(String name);

    Page<Organization> findByNameContainingIgnoreCase(String name, Pageable pageable);

    long count();
}
