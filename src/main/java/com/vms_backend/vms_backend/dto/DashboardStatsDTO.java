package com.vms_backend.vms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

	private long totalMeetingsToday;

    private long activeMeetings;

    private long completedMeetings;
}