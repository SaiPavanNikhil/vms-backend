package com.vms_backend.vms_backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class VisitorMeetingRequest {
    private String mobileNo;
    private String hostId;
    private LocalDate requestedMeetingDate;
    private LocalTime requestedMeetingTime;

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
    public String getHostId() { return hostId; }
    public void setHostId(String hostId) { this.hostId = hostId; }
    public LocalDate getRequestedMeetingDate() { return requestedMeetingDate; }
    public void setRequestedMeetingDate(LocalDate requestedMeetingDate) { this.requestedMeetingDate = requestedMeetingDate; }
    public LocalTime getRequestedMeetingTime() { return requestedMeetingTime; }
    public void setRequestedMeetingTime(LocalTime requestedMeetingTime) { this.requestedMeetingTime = requestedMeetingTime; }
}