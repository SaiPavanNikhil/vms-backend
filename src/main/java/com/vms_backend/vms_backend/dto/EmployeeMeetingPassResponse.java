package com.vms_backend.vms_backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeMeetingPassResponse {
    private String meetingId;
    private String passNo;
    private String participantName;
    private String participantOrganisation;
    private String mobileNo;
    private String meetingTitle;
    private String meetingDate;
    private String meetingTime;
    private String hostName;
    private String qrCode;
    private String photo;
}