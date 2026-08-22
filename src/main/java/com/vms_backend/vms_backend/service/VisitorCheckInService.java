package com.vms_backend.vms_backend.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vms_backend.vms_backend.dto.VisitorCheckInRequest;
import com.vms_backend.vms_backend.dto.VisitorCheckInResponse;
import com.vms_backend.vms_backend.entity.VisitorMeeting;
import com.vms_backend.vms_backend.entity.Visitors;
import com.vms_backend.vms_backend.repository.VisitorMeetingRepository;
import com.vms_backend.vms_backend.repository.VisitorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisitorCheckInService {

    private final VisitorRepository visitorsRepository;
    private final VisitorMeetingRepository visitorMeetingRepository;
    
    private static final ZoneId INDIA_ZONE =
            ZoneId.of("Asia/Kolkata");


    // =========================================================
    // API 1
    // SEARCH VISITOR BY MOBILE NUMBER
    // =========================================================

    public VisitorCheckInResponse searchVisitor(String mobileNo) {

        // -----------------------------------------
        // Validate mobile number
        // -----------------------------------------

        if (mobileNo == null || mobileNo.trim().isEmpty()) {
            throw new RuntimeException(
                    "Mobile number is required"
            );
        }

        mobileNo = mobileNo.trim();


        // -----------------------------------------
        // Find visitor
        // -----------------------------------------

        Visitors visitor = visitorsRepository
                .findById(mobileNo)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Visitor not found"
                        )
                );


        // -----------------------------------------
        // Find accepted meeting for today
        // -----------------------------------------

        LocalDate today = LocalDate.now();

        List<VisitorMeeting> meetings =
                visitorMeetingRepository
                        .findAcceptedMeetingsForToday(
                                mobileNo,
                                today
                        );


        if (meetings.isEmpty()) {
            throw new RuntimeException(
                    "No accepted meeting found for today"
            );
        }


        // -----------------------------------------
        // Select meeting
        // -----------------------------------------

        VisitorMeeting meeting = meetings.get(0);


        // -----------------------------------------
        // Build response
        // -----------------------------------------

        return buildResponse(
                visitor,
                meeting
        );
    }


    // =========================================================
// API 2
// CHECK-IN / CHECK-OUT
// =========================================================

@Transactional
public VisitorCheckInResponse checkInOut(
        VisitorCheckInRequest request) {

    // -----------------------------------------
    // Validate request
    // -----------------------------------------

    if (request == null) {
        throw new RuntimeException(
                "Request is required"
        );
    }

    if (request.getMobileNo() == null ||
            request.getMobileNo().trim().isEmpty()) {

        throw new RuntimeException(
                "Mobile number is required"
        );
    }

    if (request.getMeetingId() == null) {

        throw new RuntimeException(
                "Meeting ID is required"
        );
    }

    String mobileNo =
            request.getMobileNo().trim();


    // -----------------------------------------
    // Find visitor
    // -----------------------------------------

    Visitors visitor =
            visitorsRepository
                    .findById(mobileNo)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Visitor not found"
                            )
                    );


    // -----------------------------------------
    // Find meeting
    // -----------------------------------------

    VisitorMeeting meeting =
            visitorMeetingRepository
                    .findById(
                            request.getMeetingId()
                    )
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Meeting not found"
                            )
                    );


    // =====================================================
    // SECURITY CHECK
    // =====================================================

    if (!meeting.getMobileNo()
            .equals(visitor.getMobileNo())) {

        throw new RuntimeException(
                "This meeting does not belong to the visitor"
        );
    }


    // =====================================================
    // ACCEPTANCE CHECK
    // =====================================================

    if (!"Y".equalsIgnoreCase(
            meeting.getAcceptFlag())) {

        throw new RuntimeException(
                "Meeting has not been accepted"
        );
    }


    // =====================================================
    // APPROVED DATE CHECK
    // =====================================================

    if (meeting.getApprovedMeetingDate() == null) {

        throw new RuntimeException(
                "Approved meeting date is not available"
        );
    }

    LocalDate today =
            LocalDate.now(INDIA_ZONE);

    if (!meeting.getApprovedMeetingDate()
            .equals(today)) {

        throw new RuntimeException(
                "This meeting is not scheduled for today"
        );
    }


    // =====================================================
    // APPROVED TIME CHECK
    // =====================================================

    if (meeting.getApprovedMeetingTime() == null) {

        throw new RuntimeException(
                "Approved meeting time is not available"
        );
    }


    // =====================================================
    // CURRENT TIME - INDIA
    // =====================================================

    LocalTime currentTime =
            LocalTime.now(INDIA_ZONE);


    // =====================================================
    // CHECK-IN
    // =====================================================

    if (meeting.getEntryTime() == null) {

        LocalTime approvedTime =
                meeting.getApprovedMeetingTime();

        LocalTime checkInStart =
                approvedTime.minusMinutes(15);

        LocalTime checkInEnd =
                approvedTime.plusMinutes(15);


        // ---------------------------------------------
        // TOO EARLY
        // ---------------------------------------------

        if (currentTime.isBefore(checkInStart)) {

            throw new RuntimeException(
                    "Check-in is allowed only 15 minutes before the approved meeting time"
            );
        }


        // ---------------------------------------------
        // TOO LATE
        // ---------------------------------------------

        if (currentTime.isAfter(checkInEnd)) {

            throw new RuntimeException(
                    "Check-in time has expired. Check-in was allowed only until 15 minutes after the approved meeting time"
            );
        }


        // ---------------------------------------------
        // CHECK-IN ALLOWED
        // ---------------------------------------------

        meeting.setEntryTime(currentTime);

        visitorMeetingRepository.save(meeting);

        return buildResponse(
                visitor,
                meeting
        );
    }


    // =====================================================
    // CHECK-OUT
    // =====================================================

    if (meeting.getExitTime() == null) {

        LocalTime entryTime =
                meeting.getEntryTime();

        LocalTime earliestCheckoutTime =
                entryTime.plusHours(1);


        // ---------------------------------------------
        // TOO EARLY
        // ---------------------------------------------

        if (currentTime.isBefore(
                earliestCheckoutTime)) {

            throw new RuntimeException(
                    "Check-out is allowed only after 1 hour from check-in"
            );
        }


        // ---------------------------------------------
        // CHECK-OUT ALLOWED
        // ---------------------------------------------

        meeting.setExitTime(currentTime);

        visitorMeetingRepository.save(meeting);

        return buildResponse(
                visitor,
                meeting
        );
    }


    // =====================================================
    // ALREADY CHECKED OUT
    // =====================================================

    throw new RuntimeException(
            "Visitor has already checked out"
    );
}


    // =========================================================
    // BUILD RESPONSE
    // =========================================================

    private VisitorCheckInResponse buildResponse(
            Visitors visitor,
            VisitorMeeting meeting) {

        VisitorCheckInResponse response =
                new VisitorCheckInResponse();


        // Visitor details
        response.setMobileNo(
                visitor.getMobileNo()
        );

        response.setFirstName(
                visitor.getFirstName()
        );

        response.setLastName(
                visitor.getLastName()
        );

        response.setOrganisation(
                visitor.getOrganisation()
        );


        // Meeting details
        response.setMeetingId(
                meeting.getMeetingId()
        );

        response.setHostId(
                meeting.getHostId()
        );

        response.setApprovedMeetingDate(
                meeting.getApprovedMeetingDate()
        );

        response.setApprovedMeetingTime(
                meeting.getApprovedMeetingTime()
        );

        response.setAcceptFlag(
                meeting.getAcceptFlag()
        );


        // Check-in / Check-out details
        response.setEntryTime(
                meeting.getEntryTime()
        );

        response.setExitTime(
                meeting.getExitTime()
        );


        return response;
    }
}