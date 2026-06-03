package com.fuelac.fuelac.dto.response;

import com.fuelac.fuelac.model.entity.Organization;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrganizationResponse {

    private UUID id;
    private String codeOKUD;
    private String codeOKPO;
    private String ogrn;
    private String name;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OrganizationResponse fromEntity(Organization organization) {
        if (organization == null) {
            return null;
        }
        OrganizationResponse dto = new OrganizationResponse();
        dto.setId(organization.getId());
        dto.setCodeOKUD(organization.getCodeOKUD());
        dto.setCodeOKPO(organization.getCodeOKPO());
        dto.setOgrn(organization.getOgrn());
        dto.setName(organization.getName());
        dto.setAddress(organization.getAddress());
        dto.setCreatedAt(organization.getCreatedAt());
        dto.setUpdatedAt(organization.getUpdatedAt());
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCodeOKUD() {
        return codeOKUD;
    }

    public void setCodeOKUD(String codeOKUD) {
        this.codeOKUD = codeOKUD;
    }

    public String getCodeOKPO() {
        return codeOKPO;
    }

    public void setCodeOKPO(String codeOKPO) {
        this.codeOKPO = codeOKPO;
    }

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
