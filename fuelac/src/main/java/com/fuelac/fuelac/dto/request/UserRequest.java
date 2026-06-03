package com.fuelac.fuelac.dto.request;

import com.fuelac.fuelac.model.entity.User;
import com.fuelac.fuelac.model.enums.UserRole;

import java.util.UUID;

public class UserRequest {

    private String lastName;
    private String firstName;
    private String patronymic;
    private String email;
    private String password;
    private UserRole role;
    private UUID organizationId;

    public UserRequest() {
    }

    public UserRequest(String lastName, String firstName, String patronymic, String email, String password, UserRole role, UUID organizationId) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.email = email;
        this.password = password;
        this.role = role;
        this.organizationId = organizationId;
    }

    public User toEntity() {
        User user = new User();
        user.setLastName(this.lastName);
        user.setFirstName(this.firstName);
        user.setPatronymic(this.patronymic);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setRole(this.role);
        return user;
    }

    private String buildFullName() {
        StringBuilder sb = new StringBuilder();
        if (lastName != null) sb.append(lastName);
        if (firstName != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(firstName);
        }
        if (patronymic != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(patronymic);
        }
        return sb.toString();
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }
}
