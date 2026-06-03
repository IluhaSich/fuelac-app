package com.fuelac.fuelac.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "drivers")
public class Driver extends BaseEntity {
    private Organization organization;
    private String personnelNumber;
    private String lastName;
    private String firstName;
    private String patronymic;
    private String snils;
    private DriverLicense license;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @Column(name = "personnel_number")
    public String getPersonnelNumber() {
        return personnelNumber;
    }

    public void setPersonnelNumber(String personnelNumber) {
        this.personnelNumber = personnelNumber;
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

    @Column(name = "snils")
    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    @Embedded
    public DriverLicense getLicense() {
        return license;
    }

    public void setLicense(DriverLicense license) {
        this.license = license;
    }
}
