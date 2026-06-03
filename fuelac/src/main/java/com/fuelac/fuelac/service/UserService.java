package com.fuelac.fuelac.service;

import com.fuelac.fuelac.dto.request.UserRequest;
import com.fuelac.fuelac.dto.response.UserResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.enums.UserRole;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserResponse create(UserRequest request, AppUserDetails principal);

    Optional<UserResponse> findById(UUID id, AppUserDetails principal);

    List<UserResponse> findAll(AppUserDetails principal);

    Page<UserResponse> findAll(AppUserDetails principal, Pageable pageable);

    UserResponse update(UUID id, UserRequest request, AppUserDetails principal);

    void deleteById(UUID id, AppUserDetails principal);

    Optional<UserResponse> findByEmail(String email, AppUserDetails principal);

    List<UserResponse> findByRole(UserRole role, AppUserDetails principal);

    Page<UserResponse> findByRole(UserRole role, AppUserDetails principal, Pageable pageable);

    Page<UserResponse> search(SearchRequest request, AppUserDetails principal);
}
