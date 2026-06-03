package com.fuelac.fuelac.repository;

import com.fuelac.fuelac.model.entity.Vehicle;
import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.enums.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository extends Repository<Vehicle, UUID>, JpaSpecificationExecutor<Vehicle> {

    Vehicle save(Vehicle vehicle);

//    Optional<Vehicle> findById(UUID id);
//
//    List<Vehicle> findAll();
//
//    Page<Vehicle> findAll(Pageable pageable);

    Optional<Vehicle> findByIdAndOrganization(UUID id, Organization organization);

//    void deleteById(UUID id);

    void deleteByIdAndOrganization(UUID id, Organization organization);

    boolean existsByIdAndOrganization(UUID id, Organization organization);

    List<Vehicle> findByOrganization(Organization organization);

    Page<Vehicle> findByOrganization(Organization organization, Pageable pageable);

    List<Vehicle> findByOrganizationAndType(Organization organization, VehicleType type);

    Page<Vehicle> findByOrganizationAndType(Organization organization, VehicleType type, Pageable pageable);

//    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    Optional<Vehicle> findByRegistrationNumberAndOrganization(String registrationNumber, Organization organization);

    @Query("SELECT v FROM Vehicle v WHERE v.organization = :org AND LOWER(v.registrationNumber) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Vehicle> searchByRegistrationNumber(@Param("org") Organization organization, @Param("q") String registrationNumber, Pageable pageable);

//    Optional<Vehicle> findByGarageNumber(String garageNumber);

    Optional<Vehicle> findByGarageNumberAndOrganization(String garageNumber, Organization organization);

    @Query("SELECT v FROM Vehicle v WHERE v.organization = :org AND LOWER(v.garageNumber) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Vehicle> searchByGarageNumber(@Param("org") Organization organization, @Param("q") String garageNumber, Pageable pageable);

    @Query("SELECT v FROM Vehicle v WHERE v.organization = :org AND LOWER(v.brand) LIKE LOWER(CONCAT('%', :q, '%'))")
    Page<Vehicle> searchByBrand(@Param("org") Organization organization, @Param("q") String brand, Pageable pageable);

    @Query("SELECT v FROM Vehicle v WHERE v.organization = :org AND v.brand = :brand AND v.model = :model")
    List<Vehicle> findByBrandAndModel(@Param("org") Organization organization, @Param("brand") String brand, @Param("model") String model);

    @Query("SELECT v FROM Vehicle v WHERE v.organization = :org AND v.brand = :brand AND v.model = :model")
    Page<Vehicle> findByBrandAndModel(@Param("org") Organization organization, @Param("brand") String brand, @Param("model") String model, Pageable pageable);

    @Query("SELECT v FROM Vehicle v WHERE v.organization = :org AND v.brand = :brand AND v.model = :model AND v.year = :year")
    List<Vehicle> findByBrandAndModelAndYear(@Param("org") Organization organization, @Param("brand") String brand, @Param("model") String model, @Param("year") Integer year);
}
