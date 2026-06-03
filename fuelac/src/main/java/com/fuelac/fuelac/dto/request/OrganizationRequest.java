package com.fuelac.fuelac.dto.request;

import com.fuelac.fuelac.model.entity.Organization;

import jakarta.validation.constraints.NotBlank;
public class OrganizationRequest {

    private String codeOKUD;
    private String codeOKPO;
    private String ogrn;
    @NotBlank
    private String name;
    private String address;

    public Organization toEntity() {
        Organization org = new Organization();
        org.setCodeOKUD(this.codeOKUD);
        org.setCodeOKPO(this.codeOKPO);
        org.setOgrn(this.ogrn);
        org.setName(this.name);
        org.setAddress(this.address);
        return org;
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
}
