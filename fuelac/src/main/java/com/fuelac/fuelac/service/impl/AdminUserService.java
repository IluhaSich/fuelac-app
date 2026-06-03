package com.fuelac.fuelac.service.impl;

import com.fuelac.fuelac.dto.request.UserRequest;
import com.fuelac.fuelac.dto.response.UserResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.entity.User;
import com.fuelac.fuelac.model.enums.UserRole;
import com.fuelac.fuelac.repository.OrganizationRepository;
import com.fuelac.fuelac.repository.UserRepository;
import com.fuelac.fuelac.repository.specification.GenericSpecification;
import com.fuelac.fuelac.util.PageableUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenericSpecification genericSpecification;

    public AdminUserService(UserRepository userRepository, OrganizationRepository organizationRepository, PasswordEncoder passwordEncoder, GenericSpecification genericSpecification) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.genericSpecification = genericSpecification;
    }

    public UserResponse create(UserRequest request) {
        User user = request.toEntity();
        if (request.getOrganizationId() != null) {
            Organization org = organizationRepository.findById(request.getOrganizationId())
                    .orElseThrow(() -> new RuntimeException("Организация не найдена с id: " + request.getOrganizationId()));
            user.setOrganization(org);
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return UserResponse.fromEntity(userRepository.save(user));
    }

    public Optional<UserResponse> findById(UUID id) {
        return userRepository.findById(id).map(UserResponse::fromEntity);
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponse::fromEntity);
    }

    public UserResponse update(UUID id, UserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с id: " + id));
        existing.setLastName(request.getLastName());
        existing.setFirstName(request.getFirstName());
        existing.setPatronymic(request.getPatronymic());
        existing.setEmail(request.getEmail());
        existing.setPassword(passwordEncoder.encode(request.getPassword()));
        existing.setRole(request.getRole());
        if (request.getOrganizationId() != null) {
            Organization org = organizationRepository.findById(request.getOrganizationId())
                    .orElseThrow(() -> new RuntimeException("Организация не найдена с id: " + request.getOrganizationId()));
            existing.setOrganization(org);
        }
        return UserResponse.fromEntity(userRepository.save(existing));
    }

    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    public Optional<UserResponse> findByEmail(String email) {
        return userRepository.findByEmail(email).map(UserResponse::fromEntity);
    }

    public List<UserResponse> findByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<UserResponse> search(SearchRequest request) {
        Specification<User> spec = genericSpecification.getSpecification(request.getFilters());
        Pageable pageable = PageableUtil.fromSearchRequest(request);
        return userRepository.findAll(spec, pageable)
                .map(UserResponse::fromEntity);
    }
}
