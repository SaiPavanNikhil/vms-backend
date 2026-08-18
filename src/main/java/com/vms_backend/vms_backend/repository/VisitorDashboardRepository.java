package com.vms_backend.vms_backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vms_backend.vms_backend.dto.VisitorDashboardDTO;
import com.vms_backend.vms_backend.entity.VisitorMeeting;

@Repository
public interface VisitorDashboardRepository extends JpaRepository<VisitorMeeting, Integer> {

    @Query("""
        SELECT new com.vms_backend.vms_backend.dto.VisitorDashboardDTO(
            vm.meetingId,
            vm.mobileNo,
            CONCAT(v.firstName, ' ', COALESCE(v.lastName, '')),
            v.organisation,
            vm.hostId,
            CONCAT(e.firstName, ' ', COALESCE(e.lastName, '')),
            s.sectionName,
            vm.approvedMeetingDate,
            vm.approvedMeetingTime,
            vm.entryTime,
            vm.exitTime,
            vm.acceptFlag,
            CASE
                WHEN vm.acceptFlag='H' THEN 'Hold'
                WHEN vm.acceptFlag='Y' THEN 'Accepted'
                ELSE 'Rejected'
            END
        )
        FROM VisitorMeeting vm
        JOIN Visitors v
            ON v.mobileNo = vm.mobileNo
        JOIN Employee e
            ON e.employeeId = vm.hostId
        LEFT JOIN Section s
            ON s.sectionId = e.section.sectionId
        ORDER BY vm.meetingId DESC
        """)
    List<VisitorDashboardDTO> getVisitorDashboardData();
    
    
    // ==========================================
    @Query("""
    	    SELECT COUNT(vm)
    	    FROM VisitorMeeting vm
    	    WHERE vm.approvedMeetingDate = :today
    	    """)
    	long countTotalMeetingsToday(LocalDate today);


    	@Query("""
    	    SELECT COUNT(vm)
    	    FROM VisitorMeeting vm
    	    WHERE vm.approvedMeetingDate = :today
    	      AND vm.entryTime IS NOT NULL
    	      AND vm.exitTime IS NULL
    	    """)
    	long countActiveMeetings(LocalDate today);


    	@Query("""
    	    SELECT COUNT(vm)
    	    FROM VisitorMeeting vm
    	    WHERE vm.approvedMeetingDate = :today
    	      AND vm.exitTime IS NOT NULL
    	    """)
    	long countCompletedMeetings(LocalDate today);

}