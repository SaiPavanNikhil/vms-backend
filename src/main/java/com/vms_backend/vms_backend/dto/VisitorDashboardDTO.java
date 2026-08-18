package com.vms_backend.vms_backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitorDashboardDTO {

    private Integer meetingId;

    private String mobileNo;

    private String visitorName;

    private String organisation;

    private String hostId;

    private String hostName;

    private String sectionName;

    private LocalDate meetingDate;

    private LocalTime meetingTime;

    private LocalTime entryTime;

    private LocalTime exitTime;

    private String acceptFlag;

    private String status;
}
