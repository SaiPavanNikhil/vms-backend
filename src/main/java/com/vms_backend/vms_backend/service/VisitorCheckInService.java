package com.vms_backend.vms_backend.service;

import com.vms_backend.vms_backend.dto.VisitorCheckInRequest;
import com.vms_backend.vms_backend.dto.VisitorCheckInResponse;
import com.vms_backend.vms_backend.entity.VisitorMeeting;
import com.vms_backend.vms_backend.entity.Visitors;
import com.vms_backend.vms_backend.repository.VisitorMeetingRepository;
import com.vms_backend.vms_backend.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitorCheckInService {

    private final VisitorRepository visitorsRepository;
    private final VisitorMeetingRepository visitorMeetingRepository;


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

        Visitors visitor = visitorsRepository
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
        // Make sure meeting belongs to this visitor
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


        if (!meeting.getApprovedMeetingDate()
                .equals(LocalDate.now())) {

            throw new RuntimeException(
                    "This meeting is not scheduled for today"
            );
        }


        // =====================================================
        // CHECK-IN
        // =====================================================

        if (meeting.getEntryTime() == null) {

            LocalTime currentTime =
                    LocalTime.now();

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

            LocalTime currentTime =
                    LocalTime.now();

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