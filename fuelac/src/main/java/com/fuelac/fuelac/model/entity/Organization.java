package com.fuelac.fuelac.model.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizations")
public class Organization extends BaseEntity {
    private String codeOKUD;
    private String codeOKPO;
    private String ogrn;
    private String name;
    private String address;
    
    private List<Driver> drivers = new ArrayList<>();
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<Waybill> waybills = new ArrayList<>();

    @Column(name = "code_okud")
    public String getCodeOKUD() {
        return codeOKUD;
    }

    public void setCodeOKUD(String codeOKUD) {
        this.codeOKUD = codeOKUD;
    }

    @Column(name = "code_okpo")
    public String getCodeOKPO() {
        return codeOKPO;
    }

    public void setCodeOKPO(String codeOKPO) {
        this.codeOKPO = codeOKPO;
    }

    @Column(name = "ogrn")
    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @OneToMany(mappedBy = "organization")
    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    @OneToMany(mappedBy = "organization")
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @OneToMany(mappedBy = "organization")
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @OneToMany(mappedBy = "organization")
    public List<Waybill> getWaybills() {
        return waybills;
    }

    public void setWaybills(List<Waybill> waybills) {
        this.waybills = waybills;
    }
}
