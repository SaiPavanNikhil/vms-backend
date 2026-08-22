package com.vms_backend.vms_backend.repository;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vms_backend.vms_backend.entity.EmployeeMeetingParticipant;
import com.vms_backend.vms_backend.entity.ParticipantStatus;

@Repository
public interface EmployeeMeetingParticipantRepository
        extends JpaRepository<EmployeeMeetingParticipant, Long> {

    Optional<EmployeeMeetingParticipant> findByApprovalToken(String approvalToken);

    Optional<EmployeeMeetingParticipant> findByMeeting_MeetingIdAndParticipantMobile(
            Long meetingId, String participantMobile);
    // ADD THIS FOR HOST EMPLOYEE SEND INVITATION STATUS DASHBAORD UPDATE

    long countByMeeting_Employee_EmployeeIdAndStatus(String employeeId, ParticipantStatus status);

    @Query("""
        SELECT COUNT(p) FROM EmployeeMeetingParticipant p
        WHERE p.meeting.employee.employeeId = :employeeId
          AND p.meeting.meetingDate = :date
          AND p.status = com.vms_backend.vms_backend.entity.ParticipantStatus.APPROVED
    """)
    long countTodaysApprovedVisitors(@Param("employeeId") String employeeId, @Param("date") LocalDate date);

    @Query("""
        SELECT COUNT(p) FROM EmployeeMeetingParticipant p
        WHERE p.meeting.employee.employeeId = :employeeId
          AND p.meeting.meetingDate = :date
          AND p.status = com.vms_backend.vms_backend.entity.ParticipantStatus.APPROVED
          AND p.passNo IS NOT NULL
    """)
    long countActivePassesToday(@Param("employeeId") String employeeId, @Param("date") LocalDate date);
    
    
    //THIS CODE FOR SEND HOST EMPLOYEE SEND INVITATION STATUS DETAILS PERSOANL RECENT VISITOR DETAILS EMPLYEE DASHBAORD
 // add this method
    @Query("""
        SELECT p FROM EmployeeMeetingParticipant p
        WHERE p.meeting.employee.employeeId = :employeeId
        ORDER BY p.meeting.meetingDate DESC, p.meeting.meetingTime DESC
    """)
    List<EmployeeMeetingParticipant> findRecentByEmployeeRaw(@Param("employeeId") String employeeId);
}