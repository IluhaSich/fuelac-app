package com.fuelac.fuelac.repository;

import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.entity.User;
import com.fuelac.fuelac.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends Repository<User, UUID>, JpaSpecificationExecutor<User> {

    User save(User user);

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    List<User> findByRole(UserRole role);

    void deleteById(UUID id);

    boolean existsById(UUID id);

    Optional<User> findByIdAndOrganization(UUID id, Organization organization);

    Optional<User> findByEmailAndOrganization(String email, Organization organization);

    List<User> findByOrganization(Organization organization);

    Page<User> findByOrganization(Organization organization, Pageable pageable);

    List<User> findByOrganizationAndRole(Organization organization, UserRole role);

    Page<User> findByOrganizationAndRole(Organization organization, UserRole role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.organization = :org AND (" +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.patronymic) LIKE LOWER(CONCAT('%', :name, '%'))" +
           ")")
    List<User> searchByName(@Param("org") Organization organization, @Param("name") String name);

    @Query("SELECT u FROM User u WHERE u.organization = :org AND (" +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(u.patronymic) LIKE LOWER(CONCAT('%', :name, '%'))" +
           ")")
    Page<User> searchByName(@Param("org") Organization organization, @Param("name") String name, Pageable pageable);

    void deleteByIdAndOrganization(UUID id, Organization organization);

    boolean existsByIdAndOrganization(UUID id, Organization organization);
}
