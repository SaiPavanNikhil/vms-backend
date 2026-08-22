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
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class VisitorPassService {

    private final VisitorMeetingRepository meetingRepo;
    private final VisitorRepository visitorsRepository;
    private final EmployeeRepository employeeRepository;

    private final EncryptionService encryptionService;
    private final QrCodeService qrCodeService;

    private final String frontendUrl =
            "YOUR_FRONTEND_URL";
 
    @Transactional(readOnly = true)
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
        // Build visitor name
        // -----------------------------------------------------

        String visitorName =
                visitor.getFirstName();

        if (visitor.getLastName() != null &&
                !visitor.getLastName().isBlank()) {

            visitorName +=
                    " " + visitor.getLastName();
        }


        // -----------------------------------------------------
        // Build host name
        // -----------------------------------------------------

        String hostName =
                employee.getFirstName();

        if (employee.getLastName() != null &&
                !employee.getLastName().isBlank()) {

            hostName +=
                    " " + employee.getLastName();
        }


        // -----------------------------------------------------
        // Department
        // -----------------------------------------------------

        String department = null;

        if (employee.getSection() != null) {

            department =
                    employee.getSection().getSectionName();
        }


        // -----------------------------------------------------
        // Generate SAME encrypted pass link
        // -----------------------------------------------------

        String encryptedToken;

        try {

            encryptedToken =
                    encryptionService.encrypt(
                            meeting.getMeetingId().toString()
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to encrypt visitor pass token",
                    e
            );
        }


        String passLink =
                frontendUrl
                        + "/visitor-pass/"
                        + URLEncoder.encode(
                                encryptedToken,
                                StandardCharsets.UTF_8
                        );


        // -----------------------------------------------------
        // Generate QR
        // -----------------------------------------------------

        String qrCode;

        try {

            qrCode =
                    qrCodeService.generateQrCode(
                            passLink
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate visitor pass QR code",
                    e
            );
        }


        // -----------------------------------------------------
        // Build response
        // -----------------------------------------------------

        return VisitorPassResponse.builder()

                // =========================
                // PASS
                // =========================

                .meetingId(
                        meeting.getMeetingId()
                )

                .passNo(
                        meeting.getPassNo()
                )

                // =========================
                // VISITOR
                // =========================

                .visitorName(
                        visitorName
                )

                .mobileNo(
                        visitor.getMobileNo()
                )

                .company(
                        visitor.getOrganisation()
                )

                .address(
                        visitor.getAddress()
                )

                .purpose(
                        visitor.getPurposeOfVisit()
                )

                .photo(
                        visitor.getPhoto()
                )

                // =========================
                // DATE
                // =========================

                .visitDate(
                        meeting.getApprovedMeetingDate()
                )

                // =========================
                // HOST
                // =========================

                .hostName(
                        hostName
                )

                .hostDesignation(
                        employee.getDesignation()
                )

                .department(
                        department
                )

                // =========================
                // TIMES
                // =========================

                .requestedMeetingTime(
                        formatTime(
                                meeting.getRequestedMeetingTime()
                        )
                )

                .approvedMeetingTime(
                        formatTime(
                                meeting.getApprovedMeetingTime()
                        )
                )

                // =========================
                // QR
                // =========================

                .qrCode(
                        qrCode
                )

                .build();
    }
    
    private String formatTime(LocalTime time) {

        if (time == null) {
            return null;
        }

        return time.format(
                DateTimeFormatter.ofPattern("hh:mm a")
        );
    }
}