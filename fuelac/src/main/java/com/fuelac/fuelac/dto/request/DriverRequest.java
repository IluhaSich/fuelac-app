package com.fuelac.fuelac.dto.request;

import com.fuelac.fuelac.model.entity.Driver;
import com.fuelac.fuelac.model.entity.DriverLicense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class DriverRequest {

    @NotBlank
    private String personnelNumber;
    @NotBlank
    private String lastName;
    @NotBlank
    private String firstName;
    private String patronymic;
    private String snils; // необязательное поле
    @NotBlank
    private String licenseNumber;
    @NotBlank
    private String licenseCategory;
    @NotNull
    private LocalDate licenseIssueDate;
    @NotNull
    private LocalDate licenseExpirationDate;

    public DriverRequest() {
    }

    public DriverRequest(String personnelNumber, String lastName, String firstName, String patronymic, String snils, String licenseNumber, String licenseCategory, LocalDate licenseIssueDate, LocalDate licenseExpirationDate) {
        this.personnelNumber = personnelNumber;
        this.lastName = lastName;
        this.firstName = firstName;
        this.patronymic = patronymic;
        this.snils = snils;
        this.licenseNumber = licenseNumber;
        this.licenseCategory = licenseCategory;
        this.licenseIssueDate = licenseIssueDate;
        this.licenseExpirationDate = licenseExpirationDate;
    }

    public Driver toEntity() {
        Driver driver = new Driver();
        driver.setPersonnelNumber(this.personnelNumber);
        driver.setLastName(this.lastName);
        driver.setFirstName(this.firstName);
        driver.setPatronymic(this.patronymic);
        driver.setSnils(this.snils);

        DriverLicense license = new DriverLicense();
        license.setNumber(this.licenseNumber);
        license.setCategory(this.licenseCategory);
        license.setIssueDate(this.licenseIssueDate);
        license.setExpirationDate(this.licenseExpirationDate);
        driver.setLicense(license);

        return driver;
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
}
