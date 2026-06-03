package com.fuelac.fuelac.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;

import java.time.LocalDate;

@Embeddable
public class DriverLicense {
    private String number;
    private String category;
    private LocalDate issueDate;
    private LocalDate expirationDate;

    @Column(name = "license_number")
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Column(name = "license_category")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Column(name = "license_issue_date")
    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    @Column(name = "license_expiration_date")
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Transient
    public boolean isValid() {
        if (expirationDate == null) {
            return false;
        }
        return LocalDate.now().isBefore(expirationDate);
    }
}
