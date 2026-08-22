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

    // =========================
    // PASS
    // =========================

    private String passNo;

    // =========================
    // VISITOR DETAILS
    // =========================

    private String visitorName;

    private String mobileNo;

    private String company;

    private String address;

    private String purpose;

    private String photo;

    // =========================
    // VISIT DATE
    // =========================

    private LocalDate visitDate;

    // =========================
    // HOST DETAILS
    // =========================

    private String hostName;

    private String hostDesignation;

    private String department;

    // =========================
    // MEETING TIMES
    // =========================

    private String requestedMeetingTime;

    private String approvedMeetingTime;

    // =========================
    // QR CODE
    // =========================

    private String qrCode;
}