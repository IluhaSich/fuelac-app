package com.fuelac.fuelac.dto.response;

import com.fuelac.fuelac.model.entity.User;
import com.fuelac.fuelac.model.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserResponse {

    private UUID id;
    private String lastName;
    private String firstName;
    private String patronymic;
    private String fullName;
    private String initials;
    private String email;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponse() {
    }

    public UserResponse(UUID id, String lastName, String firstName, String patronymic, String email, UserRole role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserResponse fromEntity(User user) {
        if (user == null) {
            return null;
        }
        UserResponse response = new UserResponse(
                user.getId(),
                user.getLastName(),
                user.getFirstName(),
                user.getPatronymic(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
        response.setFullName(user.getFullName());
        response.setInitials(buildInitials(user.getLastName(), user.getFirstName(), user.getPatronymic()));
        return response;
    }

    private static String buildInitials(String lastName, String firstName, String patronymic) {
        StringBuilder sb = new StringBuilder();
        if (lastName != null) sb.append(lastName);
        if (firstName != null) sb.append(" ").append(firstName.charAt(0)).append(".");
        if (patronymic != null) sb.append(patronymic.charAt(0)).append(".");
        return sb.toString();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
