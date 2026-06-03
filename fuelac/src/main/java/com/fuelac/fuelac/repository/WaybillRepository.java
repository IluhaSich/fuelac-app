package com.fuelac.fuelac.repository;

import com.fuelac.fuelac.model.entity.Driver;
import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.entity.Vehicle;
import com.fuelac.fuelac.model.entity.VehicleFuelNorms;
import com.fuelac.fuelac.model.entity.Waybill;
import com.fuelac.fuelac.model.enums.WaybillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WaybillRepository extends Repository<Waybill, UUID>, JpaSpecificationExecutor<Waybill> {

    Waybill save(Waybill waybill);

//    Optional<Waybill> findById(UUID id);
//
//    List<Waybill> findAll();
//
//    Page<Waybill> findAll(Pageable pageable);
//
//    void deleteById(UUID id);

    Optional<Waybill> findByIdAndOrganization(UUID id, Organization organization);

    void deleteByIdAndOrganization(UUID id, Organization organization);

    boolean existsByIdAndOrganization(UUID id, Organization organization);

    List<Waybill> findByOrganization(Organization organization);

    Page<Waybill> findByOrganization(Organization organization, Pageable pageable);

    List<Waybill> findByOrganizationAndStatus(Organization organization, WaybillStatus status);

    Page<Waybill> findByOrganizationAndStatus(Organization organization, WaybillStatus status, Pageable pageable);

    Optional<Waybill> findByOrganizationAndNumber(Organization organization, String number);

    List<Waybill> findByOrganizationAndVehicle(Organization organization, Vehicle vehicle);

    Page<Waybill> findByOrganizationAndVehicle(Organization organization, Vehicle vehicle, Pageable pageable);

    List<Waybill> findByOrganizationAndDriver(Organization organization, Driver driver);

    Page<Waybill> findByOrganizationAndDriver(Organization organization, Driver driver, Pageable pageable);

    @Query("SELECT w FROM Waybill w WHERE w.organization = :org AND w.workStartDate BETWEEN :from AND :to")
    List<Waybill> findByDateRange(@Param("org") Organization organization, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT w FROM Waybill w WHERE w.organization = :org AND w.workStartDate BETWEEN :from AND :to")
    Page<Waybill> findByDateRange(@Param("org") Organization organization, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);

    @Query("SELECT w FROM Waybill w WHERE w.organization = :org AND w.vehicle = :vehicle AND w.workStartDate BETWEEN :from AND :to")
    List<Waybill> findByVehicleAndDateRange(@Param("org") Organization organization, @Param("vehicle") Vehicle vehicle, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    Page<Waybill> findByOrganizationAndVehicleAndWorkStartDateBetween(Organization organization, Vehicle vehicle, LocalDateTime from, LocalDateTime to, Pageable pageable);

    @Query("SELECT w FROM Waybill w WHERE w.organization = :org AND w.driver = :driver AND w.workStartDate BETWEEN :from AND :to")
    List<Waybill> findByDriverAndDateRange(@Param("org") Organization organization, @Param("driver") Driver driver, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT w FROM Waybill w WHERE w.organization = :org AND w.status = :status AND w.workStartDate BETWEEN :from AND :to")
    List<Waybill> findByStatusAndDateRange(@Param("org") Organization organization, @Param("status") WaybillStatus status, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT w FROM Waybill w WHERE w.organization = :org AND w.vehicle = :vehicle AND w.status = 'CLOSED' ORDER BY w.workStartDate DESC LIMIT 1")
    Optional<Waybill> findLastClosedByVehicle(@Param("org") Organization organization, @Param("vehicle") Vehicle vehicle);

    List<Waybill> findByAppliedFuelNorm(VehicleFuelNorms appliedFuelNorm);
}
