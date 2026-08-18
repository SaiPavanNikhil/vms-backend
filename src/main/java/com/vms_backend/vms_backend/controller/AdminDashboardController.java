package com.vms_backend.vms_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vms_backend.vms_backend.dto.DashboardStatsDTO;
import com.vms_backend.vms_backend.dto.VisitorDashboardDTO;
import com.vms_backend.vms_backend.service.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin-dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    /**
     * Get Visitor Dashboard Data
     */
    @GetMapping("/visitors")
    public ResponseEntity<List<VisitorDashboardDTO>> getVisitorDashboardData() {

        return ResponseEntity.ok(
                adminDashboardService.getVisitorDashboardData()
        );

    }
    
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {

        return ResponseEntity.ok(
                adminDashboardService.getDashboardStats()
        );

    }

}
