package com.fuelac.fuelac.dto.response;

import com.fuelac.fuelac.model.entity.Driver;
import com.fuelac.fuelac.model.entity.DriverLicense;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class DriverResponse {

    private UUID id;
    private String personnelNumber;
    private String lastName;
    private String firstName;
    private String patronymic;
    private String fullName;
    private String initials;
    private String snils;
    private String licenseNumber;
    private String licenseCategory;
    private LocalDate licenseIssueDate;
    private LocalDate licenseExpirationDate;
    private boolean licenseValid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DriverResponse() {
    }

    public DriverResponse(UUID id, String personnelNumber, String lastName, String firstName, String patronymic, String snils, String licenseNumber, String licenseCategory, LocalDate licenseIssueDate, LocalDate licenseExpirationDate, boolean licenseValid, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.personnelNumber = personnelNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.snils = snils;
        this.licenseNumber = licenseNumber;
        this.licenseCategory = licenseCategory;
        this.licenseIssueDate = licenseIssueDate;
        this.licenseExpirationDate = licenseExpirationDate;
        this.licenseValid = licenseValid;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static DriverResponse fromEntity(Driver driver) {
        if (driver == null) {
            return null;
        }
        DriverLicense license = driver.getLicense();
        DriverResponse response = new DriverResponse(
                driver.getId(),
                driver.getPersonnelNumber(),
                driver.getLastName(),
                driver.getFirstName(),
                driver.getPatronymic(),
                driver.getSnils(),
                license != null ? license.getNumber() : null,
                license != null ? license.getCategory() : null,
                license != null ? license.getIssueDate() : null,
                license != null ? license.getExpirationDate() : null,
                license != null && license.isValid(),
                driver.getCreatedAt(),
                driver.getUpdatedAt()
        );
        response.setFullName(driver.getFullName());
        response.setInitials(buildInitials(driver.getLastName(), driver.getFirstName(), driver.getPatronymic()));
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

    public String getPersonnelNumber() {
        return personnelNumber;
    }

    public void setPersonnelNumber(String personnelNumber) {
        this.personnelNumber = personnelNumber;
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

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseCategory() {
        return licenseCategory;
    }

    public void setLicenseCategory(String licenseCategory) {
        this.licenseCategory = licenseCategory;
    }

    public LocalDate getLicenseIssueDate() {
        return licenseIssueDate;
    }

    public void setLicenseIssueDate(LocalDate licenseIssueDate) {
        this.licenseIssueDate = licenseIssueDate;
    }

    public LocalDate getLicenseExpirationDate() {
        return licenseExpirationDate;
    }

    public void setLicenseExpirationDate(LocalDate licenseExpirationDate) {
        this.licenseExpirationDate = licenseExpirationDate;
    }

    public boolean isLicenseValid() {
        return licenseValid;
    }

    public void setLicenseValid(boolean licenseValid) {
        this.licenseValid = licenseValid;
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
