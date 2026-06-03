package com.fuelac.fuelac.repository;

import com.fuelac.fuelac.model.entity.Driver;
import com.fuelac.fuelac.model.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DriverRepository extends Repository<Driver, UUID>, JpaSpecificationExecutor<Driver> {

    Driver save(Driver driver);

    Optional<Driver> findByIdAndOrganization(UUID id, Organization organization);

    void deleteByIdAndOrganization(UUID id, Organization organization);

    boolean existsByIdAndOrganization(UUID id, Organization organization);

    List<Driver> findByOrganization(Organization organization);

    Page<Driver> findByOrganization(Organization organization, Pageable pageable);

    Optional<Driver> findByPersonnelNumberAndOrganization(String personnelNumber, Organization organization);

    @Query("SELECT d FROM Driver d WHERE d.organization = :org AND (" +
           "LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(d.patronymic) LIKE LOWER(CONCAT('%', :name, '%'))" +
           ")")
    List<Driver> searchByName(@Param("org") Organization organization, @Param("name") String name);

    @Query("SELECT d FROM Driver d WHERE d.organization = :org AND (" +
           "LOWER(d.lastName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(d.patronymic) LIKE LOWER(CONCAT('%', :name, '%'))" +
           ")")
    Page<Driver> searchByName(@Param("org") Organization organization, @Param("name") String name, Pageable pageable);

    @Query("SELECT d FROM Driver d WHERE d.organization = :org AND LOWER(d.snils) LIKE LOWER(CONCAT('%', :snils, '%'))")
    List<Driver> searchBySnils(@Param("org") Organization organization, @Param("snils") String snils);

    @Query("SELECT d FROM Driver d WHERE d.organization = :org AND LOWER(d.snils) LIKE LOWER(CONCAT('%', :snils, '%'))")
    Page<Driver> searchBySnils(@Param("org") Organization organization, @Param("snils") String snils, Pageable pageable);
}
