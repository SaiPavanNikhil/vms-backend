package com.vms_backend.vms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorPassResponse {

    private Integer meetingId;

    private String passNo;

    private String visitorName;

    private String company;

    private String purpose;

    private String mobileNo;

    private String photo;

    private LocalDate visitDate;

    private String hostName;

    private String hostDesignation;
}