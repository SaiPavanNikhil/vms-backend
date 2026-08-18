package com.vms_backend.vms_backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee_meeting_participant")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeMeetingParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private EmployeeMeeting meeting;


    @Column(name = "participant_name", nullable = false, length = 100)
    private String participantName;


    @Column(name = "participant_email", nullable = false, length = 100)
    private String participantEmail;


    @Column(name = "participant_mobile", nullable = false, length = 10)
    private String participantMobile;

    @Column(name = "participant_organisation", length = 50)
    private String participantOrganisation;
    
    @Column(
    	    name = "acceptance_status",
    	    length = 20
    	)
   private String acceptanceStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ParticipantStatus status = ParticipantStatus.PENDING;
    
    @Column(name = "approval_token", unique = true, length = 100)
    private String approvalToken;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
}