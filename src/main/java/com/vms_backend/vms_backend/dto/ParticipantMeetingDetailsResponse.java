package com.vms_backend.vms_backend.dto;

public class ParticipantMeetingDetailsResponse {
    private String participantName;
    private String participantEmail;
    private String participantMobile;
    private String organizerName;
    private String meetingTitle;
    private String meetingDate;
    private String meetingTime;
    private String status;

    public String getParticipantName() { return participantName; }
    public void setParticipantName(String v) { this.participantName = v; }
    public String getParticipantEmail() { return participantEmail; }
    public void setParticipantEmail(String v) { this.participantEmail = v; }
    public String getParticipantMobile() { return participantMobile; }
    public void setParticipantMobile(String v) { this.participantMobile = v; }
    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String v) { this.organizerName = v; }
    public String getMeetingTitle() { return meetingTitle; }
    public void setMeetingTitle(String v) { this.meetingTitle = v; }
    public String getMeetingDate() { return meetingDate; }
    public void setMeetingDate(String v) { this.meetingDate = v; }
    public String getMeetingTime() { return meetingTime; }
    public void setMeetingTime(String v) { this.meetingTime = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
}