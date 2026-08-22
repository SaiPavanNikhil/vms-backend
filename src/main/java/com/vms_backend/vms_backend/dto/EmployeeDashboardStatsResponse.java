package com.vms_backend.vms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDashboardStatsResponse {
    private long todaysVisitors;
    private long appointmentsToday;
    private long activePasses;
    private long pendingRequests;
}