package com.fuelac.fuelac.infrastructure;

import com.fuelac.fuelac.model.entity.Organization;
import com.fuelac.fuelac.model.entity.User;
import com.fuelac.fuelac.model.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AppUserDetails implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final String fullName;
    private final UserRole role;
    private final Organization organization;
    private final boolean active;

    public AppUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.fullName = user.getFullName();
        this.role = user.getRole();
        this.organization = user.getOrganization();
        this.active = true;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public UserRole getRole() {
        return role;
    }

    public Organization getOrganization() {
        return organization;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isOrganizationManager() {
        return role == UserRole.ORGANIZATION_MANAGER;
    }

    public boolean isDispatcher() {
        return role == UserRole.DISPATCHER;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
