package com.vms_backend.vms_backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vms_backend.vms_backend.dto.DashboardStatsDTO;
import com.vms_backend.vms_backend.dto.VisitorDashboardDTO;
import com.vms_backend.vms_backend.repository.VisitorDashboardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final VisitorDashboardRepository visitorDashboardRepository;

    /**
     * Get Visitor Dashboard Data
     */
    public List<VisitorDashboardDTO> getVisitorDashboardData() {

        return visitorDashboardRepository.getVisitorDashboardData();

    }
    
    /**
     * Get Dashboard Statistics
     */
    public DashboardStatsDTO getDashboardStats() {
	
	    LocalDate today = LocalDate.now();
	
	    long totalMeetingsToday =
	            visitorDashboardRepository
	                    .countTotalMeetingsToday(today);
	
	    long activeMeetings =
	            visitorDashboardRepository
	                    .countActiveMeetings(today);
	
	    long completedMeetings =
	            visitorDashboardRepository
	                    .countCompletedMeetings(today);
	
	    return new DashboardStatsDTO(
	            totalMeetingsToday,
	            activeMeetings,
	            completedMeetings
	    );
	}

}
