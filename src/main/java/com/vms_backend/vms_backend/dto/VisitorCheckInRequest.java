package com.vms_backend.vms_backend.dto;

public class VisitorCheckInRequest {

    private String mobileNo;

    private Integer meetingId;


    // =========================
    // GETTERS AND SETTERS
    // =========================

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }


    public Integer getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Integer meetingId) {
        this.meetingId = meetingId;
    }
}