package com.fuelac.fuelac.service.impl;

import com.fuelac.fuelac.dto.request.UserRequest;
import com.fuelac.fuelac.dto.response.UserResponse;
import com.fuelac.fuelac.dto.search.SearchRequest;
import com.fuelac.fuelac.infrastructure.AppUserDetails;
import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.entity.User;
import com.fuelac.fuelac.model.enums.UserRole;
import com.fuelac.fuelac.repository.OrganizationRepository;
import com.fuelac.fuelac.repository.UserRepository;
import com.fuelac.fuelac.repository.specification.GenericSpecification;
import com.fuelac.fuelac.service.UserService;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final GenericSpecification genericSpecification;

    public UserServiceImpl(UserRepository userRepository, OrganizationRepository organizationRepository, PasswordEncoder passwordEncoder, GenericSpecification genericSpecification) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.genericSpecification = genericSpecification;
    }

    private Organization requireOrganization(AppUserDetails principal) {
        Organization org = principal.getOrganization();
        if (org == null) {
            throw new IllegalStateException("Организация обязательна для данной операции");
        }
        return org;
    }

    @Override
    public UserResponse create(UserRequest request, AppUserDetails principal) {
        User user = request.toEntity();
        Organization org = request.getOrganizationId() != null
                ? organizationRepository.findById(request.getOrganizationId())
                        .orElseThrow(() -> new RuntimeException("Организация не найдена с id: " + request.getOrganizationId()))
                : requireOrganization(principal);
        user.setOrganization(org);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    @Override
    public Optional<UserResponse> findById(UUID id, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return userRepository.findByIdAndOrganization(id, org)
                .map(UserResponse::fromEntity);
    }

    @Override
    public List<UserResponse> findAll(AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return userRepository.findByOrganization(org).stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserResponse> findAll(AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return userRepository.findByOrganization(org, pageable)
                .map(UserResponse::fromEntity);
    }

    @Override
    public UserResponse update(UUID id, UserRequest request, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        User existing = userRepository.findByIdAndOrganization(id, org)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с id: " + id));
        existing.setLastName(request.getLastName());
        existing.setFirstName(request.getFirstName());
        existing.setPatronymic(request.getPatronymic());
        existing.setEmail(request.getEmail());
        existing.setPassword(passwordEncoder.encode(request.getPassword()));
        existing.setRole(request.getRole());
        return UserResponse.fromEntity(userRepository.save(existing));
    }

    @Override
    public void deleteById(UUID id, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        userRepository.deleteByIdAndOrganization(id, org);
    }

    @Override
    public Optional<UserResponse> findByEmail(String email, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return userRepository.findByEmailAndOrganization(email, org)
                .map(UserResponse::fromEntity);
    }

    @Override
    public List<UserResponse> findByRole(UserRole role, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        return userRepository.findByOrganizationAndRole(org, role).stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserResponse> findByRole(UserRole role, AppUserDetails principal, Pageable pageable) {
        Organization org = requireOrganization(principal);
        return userRepository.findByOrganizationAndRole(org, role, pageable)
                .map(UserResponse::fromEntity);
    }

    @Override
    public Page<UserResponse> search(SearchRequest request, AppUserDetails principal) {
        Organization org = requireOrganization(principal);
        Specification<User> orgSpec = (root, query, cb) -> cb.equal(root.get("organization"), org);
        Specification<User> filterSpec = genericSpecification.getSpecification(request.getFilters());
        Pageable pageable = PageableUtil.fromSearchRequest(request);
        return userRepository.findAll(Specification.where(orgSpec).and(filterSpec), pageable)
                .map(UserResponse::fromEntity);
    }
}
