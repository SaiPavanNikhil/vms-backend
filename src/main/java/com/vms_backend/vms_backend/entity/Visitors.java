package com.vms_backend.vms_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "visitors")
public class Visitors {

    @Id
    @Column(name = "mobileno", length = 10)
    private String mobileNo;

    @Column(name = "firstname", length = 15, nullable = false)
    private String firstName;

    @Column(name = "lastname", length = 20)
    private String lastName;

    @Column(name = "address", length = 30, nullable = true)
    private String address;

    @Column(name = "state", length = 10, nullable = false)
    private String state;

    @Column(name = "district", length = 20, nullable = false)
    private String district;

    @Column(name = "organisation", length = 50)
    private String organisation;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "photo", length = 255)
    private String photo;

    @Column(name = "designation", length = 30)
    private String designation;

    @Column(name = "registrationdate", nullable = false)
    private LocalDate registrationDate;

    
	@Column(name = "mode_of_visit", length = 10, nullable = false)
    private String modeOfVisit;
    @Column(name = "purpose_of_visit", length = 30, nullable = false)
    private String purposeOfVisit;
    // getters & setters
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
    public String getModeOfVisit() { return modeOfVisit; }
    public void setModeOfVisit(String modeOfVisit) { this.modeOfVisit = modeOfVisit; }
    public String getPurposeOfVisit() { return purposeOfVisit; }
    public void setPurposeOfVisit(String purposeOfVisit) { this.purposeOfVisit = purposeOfVisit; }
    public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
}