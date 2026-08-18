package com.vms_backend.vms_backend.service;

import com.vms_backend.vms_backend.dto.VisitorPassResponse;
import com.vms_backend.vms_backend.entity.Employee;
import com.vms_backend.vms_backend.entity.VisitorMeeting;
import com.vms_backend.vms_backend.entity.Visitors;
import com.vms_backend.vms_backend.repository.EmployeeRepository;
import com.vms_backend.vms_backend.repository.VisitorMeetingRepository;
import com.vms_backend.vms_backend.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitorPassService {

    private final VisitorMeetingRepository meetingRepo;
    private final VisitorRepository visitorsRepository;
    private final EmployeeRepository employeeRepository;


    // =========================================================
    // GET VISITOR PASS
    // =========================================================

    public VisitorPassResponse getVisitorPass(
            Integer meetingId) {

        // -----------------------------------------------------
        // Find approved meeting
        // -----------------------------------------------------

        VisitorMeeting meeting =
                meetingRepo.findByMeetingIdAndAcceptFlag(
                        meetingId,
                        "Y"
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Approved meeting not found"
                        )
                );


        // -----------------------------------------------------
        // Find visitor
        // -----------------------------------------------------

        Visitors visitor =
                visitorsRepository.findById(
                        meeting.getMobileNo()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Visitor not found"
                        )
                );


        // -----------------------------------------------------
        // Find host / employee
        // -----------------------------------------------------

        Employee employee =
                employeeRepository.findById(
                        meeting.getHostId()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Host employee not found"
                        )
                );


        // -----------------------------------------------------
        // Build host name
        // -----------------------------------------------------

        String hostName =
                employee.getFirstName();

        if (employee.getLastName() != null &&
                !employee.getLastName().isBlank()) {

            hostName += " "
                    + employee.getLastName();
        }


        // -----------------------------------------------------
        // Build visitor name
        // -----------------------------------------------------

        String visitorName =
                visitor.getFirstName();

        if (visitor.getLastName() != null &&
                !visitor.getLastName().isBlank()) {

            visitorName += " "
                    + visitor.getLastName();
        }


        // -----------------------------------------------------
        // Build response
        // -----------------------------------------------------

        return VisitorPassResponse.builder()

                .meetingId(
                        meeting.getMeetingId()
                )

                .passNo(
                        meeting.getPassNo()
                )

                .visitorName(
                        visitorName
                )

                .company(
                        visitor.getOrganisation()
                )

                .purpose(
                        visitor.getPurposeOfVisit()
                )

                .mobileNo(
                        visitor.getMobileNo()
                )

                .photo(
                        visitor.getPhoto()
                )

                .visitDate(
                        meeting.getApprovedMeetingDate()
                )

                .hostName(
                        hostName
                )

                .hostDesignation(
                        employee.getDesignation()
                )

                .build();
    }
}