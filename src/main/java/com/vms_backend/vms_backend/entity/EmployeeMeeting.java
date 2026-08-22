package com.vms_backend.vms_backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee_meeting")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeMeeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long meetingId;


    // Employee who scheduled the meeting
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeid", nullable = false)
    private Employee employee;


    @Column(name = "meeting_title", nullable = false, length = 100)
    private String meetingTitle;


    @Column(name = "meeting_date", nullable = false)
    private LocalDate meetingDate;


    @Column(name = "meeting_time", nullable = false)
    private LocalTime meetingTime;


    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "meeting_purpose", length = 255)
    private String meetingPurpose;
    
    @Column(name = "venue", length = 255)
    private String venue;

    // Participants of this meeting
    @OneToMany(
        mappedBy = "meeting",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @Builder.Default
    private List<EmployeeMeetingParticipant> participants =
            new ArrayList<>();
}