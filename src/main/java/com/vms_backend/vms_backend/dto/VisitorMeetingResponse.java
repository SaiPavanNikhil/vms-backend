package com.vms_backend.vms_backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class VisitorMeetingResponse {
    private Integer meetingId;
    private String mobileNo;
    private String visitorName;
    private String hostId;
    private String hostName;
    private LocalDate requestedMeetingDate;
    private LocalTime requestedMeetingTime;
    private String acceptFlag;
    private LocalDate approvedMeetingDate;
    private LocalTime approvedMeetingTime;

    public Integer getMeetingId() { return meetingId; }
    public void setMeetingId(Integer meetingId) { this.meetingId = meetingId; }
    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }
    public String getHostId() { return hostId; }
    public void setHostId(String hostId) { this.hostId = hostId; }
    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }
    public LocalDate getRequestedMeetingDate() { return requestedMeetingDate; }
    public void setRequestedMeetingDate(LocalDate d) { this.requestedMeetingDate = d; }
    public LocalTime getRequestedMeetingTime() { return requestedMeetingTime; }
    public void setRequestedMeetingTime(LocalTime t) { this.requestedMeetingTime = t; }
    public String getAcceptFlag() { return acceptFlag; }
    public void setAcceptFlag(String acceptFlag) { this.acceptFlag = acceptFlag; }
    public LocalDate getApprovedMeetingDate() { return approvedMeetingDate; }
    public void setApprovedMeetingDate(LocalDate d) { this.approvedMeetingDate = d; }
    public LocalTime getApprovedMeetingTime() { return approvedMeetingTime; }
    public void setApprovedMeetingTime(LocalTime t) { this.approvedMeetingTime = t; }
}