package com.vms_backend.vms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentVisitorResponse {
    private String visitorName;
    private String purpose;
    private String hostName;
    private String time;      // e.g. "10:30 AM"
    private String status;    // e.g. "Scheduled", "Approved", "Rejected"
}