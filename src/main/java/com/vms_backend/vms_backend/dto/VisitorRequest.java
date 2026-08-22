package com.vms_backend.vms_backend.dto;

public class VisitorRequest {
    private String mobileNo;
    private String firstName;
    private String lastName;
    private String address;
    private String state;
    private String district;
    private String organisation;
    private String email;
    private String photoDataUrl;
    private String modeOfVisit;// e.g. "data:image/jpeg;base64,....."
    private String purposeOfVisit;
    private String descriptionOfVisit;

    public String getDescriptionOfVisit() {
		return descriptionOfVisit;
	}
	public void setDescriptionOfVisit(String descriptionOfVisit) {
		this.descriptionOfVisit = descriptionOfVisit;
	}
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
    public String getPhotoDataUrl() { return photoDataUrl; }
    public void setPhotoDataUrl(String photoDataUrl) { this.photoDataUrl = photoDataUrl; }
    public String getModeOfVisit() { return modeOfVisit; }
    public void setModeOfVisit(String modeOfVisit) { this.modeOfVisit = modeOfVisit; }
    public String getPurposeOfVisit() { return purposeOfVisit; }
    public void setPurposeOfVisit(String purposeOfVisit) { this.purposeOfVisit = purposeOfVisit; }
}