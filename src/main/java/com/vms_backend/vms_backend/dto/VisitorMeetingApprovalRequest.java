package com.vms_backend.vms_backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class VisitorMeetingApprovalRequest {
    private LocalDate approvedMeetingDate;
    private LocalTime approvedMeetingTime;

    public LocalDate getApprovedMeetingDate() { return approvedMeetingDate; }
    public void setApprovedMeetingDate(LocalDate d) { this.approvedMeetingDate = d; }
    public LocalTime getApprovedMeetingTime() { return approvedMeetingTime; }
    public void setApprovedMeetingTime(LocalTime t) { this.approvedMeetingTime = t; }
}