package com.vms_backend.vms_backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vms_backend.vms_backend.entity.VisitorMeeting;

public interface VisitorMeetingRepository extends JpaRepository<VisitorMeeting, Integer> {
    List<VisitorMeeting> findByMobileNo(String mobileNo);
    List<VisitorMeeting> findByHostId(String hostId);
    List<VisitorMeeting> findByHostIdOrderByMeetingIdDesc(String hostId);
    List<VisitorMeeting> findAllByOrderByMeetingIdDesc();

    // Latest request for a specific host+mobileNo pair (used once we already
    // have both, e.g. after decoding a token).
    Optional<VisitorMeeting> findFirstByHostIdAndMobileNoOrderByMeetingIdDesc(String hostId, String mobileNo);

    // Latest request for a host regardless of which visitor it's from — used
    // when only hostId is known (e.g. bare ?hostId=EMP001 link) and we need
    // to find that host's single most recent request to redirect into.
    Optional<VisitorMeeting> findFirstByHostIdOrderByMeetingIdDesc(String hostId);
    
    @Query("""
            SELECT vm
            FROM VisitorMeeting vm
            WHERE vm.mobileNo = :mobileNo
              AND vm.acceptFlag = 'Y'
              AND vm.approvedMeetingDate = :today
            ORDER BY vm.approvedMeetingTime DESC
        """)
        List<VisitorMeeting> findAcceptedMeetingsForToday(
                @Param("mobileNo") String mobileNo,
                @Param("today") LocalDate today
        );
    
    @Query(value = """
    	    SELECT COALESCE(
    	        MAX(
    	            CAST(
    	                SUBSTRING(passno FROM '[0-9]+$')
    	                AS INTEGER
    	            )
    	        ),
    	        0
    	    )
    	    FROM visitor_meetings
    	    WHERE approvedmeetingdate = :approvedDate
    	    """, nativeQuery = true)
    	Integer findMaxPassSequenceByDate(
    	        @Param("approvedDate") LocalDate approvedDate
    	);
    
    Optional<VisitorMeeting> findByMeetingIdAndAcceptFlag(
            Integer meetingId,
            String acceptFlag
    );
}
