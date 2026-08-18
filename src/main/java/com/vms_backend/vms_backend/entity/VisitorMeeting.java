package com.vms_backend.vms_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "visitor_meetings")
public class VisitorMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meetingid")
    private Integer meetingId;

    @Column(name = "mobileno", length = 10, nullable = false)
    private String mobileNo;

    @Column(name = "hostid", length = 10, nullable = false)
    private String hostId;

    @Column(name = "requestedmeetingdate")
    private LocalDate requestedMeetingDate;

    @Column(name = "requestedmeetingtime")
    private LocalTime requestedMeetingTime;

    @Column(name = "acceptflag", length = 1)
    private String acceptFlag = "N";

    @Column(name = "approvedmeetingdate")
    private LocalDate approvedMeetingDate;

    @Column(name = "approvedmeetingtime")
    private LocalTime approvedMeetingTime;

    @Column(name = "entrytime")
    private LocalTime entryTime;

    @Column(name = "exittime")
    private LocalTime exitTime;

    @Column(name = "timechangeapproval")
    private LocalTime timeChangeApproval;
    
    @Column(name = "passno", length = 30, unique = true)
    private String passNo;

    // getters & setters
    public Integer getMeetingId() { return meetingId; }
    public void setMeetingId(Integer meetingId) { this.meetingId = meetingId; }
    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
    public String getHostId() { return hostId; }
    public void setHostId(String hostId) { this.hostId = hostId; }
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
    public LocalTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalTime entryTime) { this.entryTime = entryTime; }
    public LocalTime getExitTime() { return exitTime; }
    public void setExitTime(LocalTime exitTime) { this.exitTime = exitTime; }
    public LocalTime getTimeChangeApproval() { return timeChangeApproval; }
    public void setTimeChangeApproval(LocalTime t) { this.timeChangeApproval = t; }
    public String getPassNo() {
        return passNo;
    }

    public void setPassNo(String passNo) {
        this.passNo = passNo;
    }
}