package com.fuelac.fuelac.repository;

import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.entity.Vehicle;
import com.fuelac.fuelac.model.entity.VehicleFuelNorms;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleFuelNormsRepository extends Repository<VehicleFuelNorms, UUID>, JpaSpecificationExecutor<VehicleFuelNorms> {

    VehicleFuelNorms save(VehicleFuelNorms norm);

//    Optional<VehicleFuelNorms> findById(UUID id);
//
//    List<VehicleFuelNorms> findAll();

    Optional<VehicleFuelNorms> findByIdAndOrganization(UUID id, Organization organization);

//    void deleteById(UUID id);

    void deleteByIdAndOrganization(UUID id, Organization organization);

    boolean existsByIdAndOrganization(UUID id, Organization organization);

    List<VehicleFuelNorms> findByOrganization(Organization organization);

    Page<VehicleFuelNorms> findByOrganization(Organization organization, Pageable pageable);

    List<VehicleFuelNorms> findByOrganizationAndVehicle(Organization organization, Vehicle vehicle);

    Page<VehicleFuelNorms> findByOrganizationAndVehicle(Organization organization, Vehicle vehicle, Pageable pageable);

    @Query("SELECT n FROM VehicleFuelNorms n WHERE n.organization = :org AND n.vehicle = :vehicle AND n.validFrom <= :date AND n.validTo >= :date")
    Optional<VehicleFuelNorms> findActiveByVehicleAndDate(
            @Param("org") Organization organization, @Param("vehicle") Vehicle vehicle, @Param("date") LocalDate date);

    @Query("SELECT n FROM VehicleFuelNorms n WHERE n.organization = :org AND n.vehicle = :vehicle AND n.validFrom <= CURRENT_DATE AND n.validTo >= CURRENT_DATE")
    Optional<VehicleFuelNorms> findActiveByVehicle(@Param("org") Organization organization, @Param("vehicle") Vehicle vehicle);

    @Query("SELECT n FROM VehicleFuelNorms n WHERE n.organization = :org AND n.vehicle = :vehicle AND n.validFrom <= CURRENT_DATE AND n.validTo >= CURRENT_DATE")
    Page<VehicleFuelNorms> findActiveByVehicle(@Param("org") Organization organization, @Param("vehicle") Vehicle vehicle, Pageable pageable);

    @Query("SELECT n FROM VehicleFuelNorms n WHERE n.organization = :org AND n.vehicle = :vehicle AND (n.validTo < CURRENT_DATE OR n.validFrom > CURRENT_DATE)")
    List<VehicleFuelNorms> findInactiveByVehicle(@Param("org") Organization organization, @Param("vehicle") Vehicle vehicle);

    @Query("SELECT n FROM VehicleFuelNorms n WHERE n.organization = :org AND n.vehicle = :vehicle AND (n.validTo < CURRENT_DATE OR n.validFrom > CURRENT_DATE)")
    Page<VehicleFuelNorms> findInactiveByVehicle(@Param("org") Organization organization, @Param("vehicle") Vehicle vehicle, Pageable pageable);
}
