package com.vms_backend.vms_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "visitors_modification_history")
public class VisitorHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historyid")
    private Integer historyId;

    @Column(name = "mobileno", length = 10, nullable = false)
    private String mobileNo;

    @Column(name = "firstname", length = 15)
    private String firstName;

    @Column(name = "lastname", length = 20)
    private String lastName;

    @Column(name = "address", length = 30)
    private String address;

    @Column(name = "state", length = 10)
    private String state;

    @Column(name = "district", length = 20)
    private String district;

    @Column(name = "organisation", length = 50)
    private String organisation;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "photo", length = 255)
    private String photo;

    @Column(name = "registrationdate")
    private LocalDate registrationDate;

    @Column(name = "changedate", nullable = false)
    private LocalDate changeDate;

    // getters & setters
    public Integer getHistoryId() { return historyId; }
    public void setHistoryId(Integer historyId) { this.historyId = historyId; }
    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getOrganisation() { return organisation; }
    public void setOrganisation(String organisation) { this.organisation = organisation; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
    public LocalDate getChangeDate() { return changeDate; }
    public void setChangeDate(LocalDate changeDate) { this.changeDate = changeDate; }
}