package com.fuelac.fuelac.model.entity;

import com.fuelac.fuelac.model.enums.UserRole;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
    private Organization organization;
    private UserRole role;
    private String lastName;
    private String firstName;
    private String patronymic;
    private String password;
    private String email;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "varchar(255)")
    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Column(name = "last_name", nullable = false)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "first_name", nullable = false)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "patronymic")
    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    @Transient
    public String getFullName() {
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

    @Column(name = "password", nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "email", nullable = false, unique = true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
