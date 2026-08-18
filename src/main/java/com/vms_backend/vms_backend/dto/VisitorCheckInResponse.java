package com.vms_backend.vms_backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class VisitorCheckInResponse {

    private String mobileNo;
    private String firstName;
    private String lastName;
    private String organisation;

    private Integer meetingId;
    private String hostId;

    private LocalDate approvedMeetingDate;
    private LocalTime approvedMeetingTime;

    private String acceptFlag;

    private LocalTime entryTime;
    private LocalTime exitTime;


    // =========================
    // GETTERS AND SETTERS
    // =========================

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }


    public Integer getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Integer meetingId) {
        this.meetingId = meetingId;
    }


    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }


    public LocalDate getApprovedMeetingDate() {
        return approvedMeetingDate;
    }

    public void setApprovedMeetingDate(LocalDate approvedMeetingDate) {
        this.approvedMeetingDate = approvedMeetingDate;
    }


    public LocalTime getApprovedMeetingTime() {
        return approvedMeetingTime;
    }

    public void setApprovedMeetingTime(LocalTime approvedMeetingTime) {
        this.approvedMeetingTime = approvedMeetingTime;
    }


    public String getAcceptFlag() {
        return acceptFlag;
    }

    public void setAcceptFlag(String acceptFlag) {
        this.acceptFlag = acceptFlag;
    }


    public LocalTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalTime entryTime) {
        this.entryTime = entryTime;
    }


    public LocalTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalTime exitTime) {
        this.exitTime = exitTime;
    }
}