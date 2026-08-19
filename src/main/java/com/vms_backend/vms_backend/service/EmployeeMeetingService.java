package com.vms_backend.vms_backend.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vms_backend.vms_backend.dto.EmployeeMeetingRequestDTO;
import com.vms_backend.vms_backend.dto.ParticipantMeetingDetailsResponse;
import com.vms_backend.vms_backend.entity.Employee;
import com.vms_backend.vms_backend.entity.EmployeeMeeting;
import com.vms_backend.vms_backend.entity.EmployeeMeetingParticipant;
import com.vms_backend.vms_backend.entity.ParticipantStatus;
import com.vms_backend.vms_backend.repository.EmployeeMeetingParticipantRepository;
import com.vms_backend.vms_backend.repository.EmployeeMeetingRepository;
import com.vms_backend.vms_backend.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeMeetingService {

    private final EmployeeMeetingRepository employeeMeetingRepository;

    private final EmployeeRepository employeeRepository;
    
    private final EmployeeMeetingParticipantRepository participantRepository;

//    private final EmailService emailService;
    private final ResendEmailService resendEmailService;


    @Transactional
    public String scheduleMeeting(EmployeeMeetingRequestDTO request) {

        // ==========================================
        // 1. FIND EMPLOYEE WHO SCHEDULED THE MEETING
        // ==========================================

        Employee employee = employeeRepository
                .findById(request.getEmployeeId())
                .orElseThrow(() ->
                        new RuntimeException("Employee not found")
                );


        // ==========================================
        // 2. VALIDATE PARTICIPANTS
        // ==========================================

        if (request.getParticipants() == null ||
            request.getParticipants().isEmpty()) {

            throw new RuntimeException(
                    "At least one participant is required"
            );
        }


        // ==========================================
        // 3. VALIDATE MEETING DETAILS
        // ==========================================

        if (request.getMeetingTitle() == null ||
            request.getMeetingTitle().isBlank()) {

            throw new RuntimeException(
                    "Meeting title is required"
            );
        }


        if (request.getMeetingDate() == null) {

            throw new RuntimeException(
                    "Meeting date is required"
            );
        }


        if (request.getMeetingTime() == null) {

            throw new RuntimeException(
                    "Meeting time is required"
            );
        }


        // ==========================================
        // 4. CREATE MEETING
        // ==========================================

        EmployeeMeeting meeting = EmployeeMeeting.builder()

                .employee(employee)

                .meetingTitle(
                        request.getMeetingTitle().trim()
                )

                .meetingPurpose(
                		request.getMeetingPurpose()
                		)

                .meetingDate(
                        request.getMeetingDate()
                )

                .meetingTime(
                        request.getMeetingTime()
                )

                .createdAt(
                        LocalDateTime.now()
                )

                .build();


        // ==========================================
        // 5. CREATE PARTICIPANTS
        // ==========================================

        for (
            EmployeeMeetingRequestDTO.ParticipantDTO participantDTO
            : request.getParticipants()
        ) {

            if (participantDTO.getName() == null ||
                participantDTO.getName().isBlank()) {

                throw new RuntimeException(
                        "Participant name is required"
                );
            }


            if (participantDTO.getEmail() == null ||
                participantDTO.getEmail().isBlank()) {

                throw new RuntimeException(
                        "Participant email is required"
                );
            }
            
            if (participantDTO.getOrganisation() == null ||
            		participantDTO.getOrganisation().isBlank()) {
            	
            	throw new RuntimeException(
            			"Participant organisation is required"
            			);
            }


            if (participantDTO.getMobileNo() == null ||
                participantDTO.getMobileNo().isBlank()) {

                throw new RuntimeException(
                        "Participant mobile number is required"
                );
            }


            EmployeeMeetingParticipant participant =
                    EmployeeMeetingParticipant.builder()

                    .meeting(meeting)

                    .participantName(
                            participantDTO.getName().trim()
                    )

                    .participantEmail(
                            participantDTO.getEmail().trim()
                    )

                    .participantMobile(
                            participantDTO.getMobileNo().trim()
                    )

                    .participantOrganisation(
                    		participantDTO.getOrganisation().trim()
                    		)

                    .build();


            // Attach participant to meeting
            meeting.getParticipants().add(participant);
        }


        // ==========================================
        // 6. SAVE MEETING + PARTICIPANTS
        // ==========================================

        employeeMeetingRepository.save(meeting);


        // ==========================================
        // 7. GET ORGANIZER NAME
        // ==========================================

        String organizerName =
                employee.getFirstName();


        if (employee.getLastName() != null &&
            !employee.getLastName().trim().isEmpty()) {

            organizerName +=
                    " " + employee.getLastName();
        }


        // ==========================================
        // 8. SEND EMAIL TO EACH PARTICIPANT
        // ==========================================

        for (
            EmployeeMeetingRequestDTO.ParticipantDTO participantDTO
            : request.getParticipants()
        ) {

            String message =
                    "You have been invited to a meeting "
                    + "scheduled by "
                    + organizerName
                    + ".";


            resendEmailService.sendMeetingStatusEmail(

                    // To
                    participantDTO.getEmail(),

                    // Participant name
                    participantDTO.getName(),

                    // Host / organizer
                    organizerName,

                    // Status
                    "Meeting Scheduled",

                    // Status color
                    "#1769aa",

                    // Date
                    request.getMeetingDate().toString(),

                    // Time
                    request.getMeetingTime().toString(),

                    // Message
                    message
            );
        }


        // ==========================================
        // 9. RETURN SUCCESS
        // ==========================================

        return "Meeting scheduled successfully";
    }
    
    @Transactional
    public String respondToInvitation(String token, String action) {

        EmployeeMeetingParticipant participant = participantRepository
                .findByApprovalToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired link"));

        if (participant.getStatus() != ParticipantStatus.PENDING) {
            return "You have already responded to this invitation";
        }

        if ("approve".equalsIgnoreCase(action)) {
            participant.setStatus(ParticipantStatus.APPROVED);
        } else if ("reject".equalsIgnoreCase(action)) {
            participant.setStatus(ParticipantStatus.REJECTED);
        } else {
            throw new RuntimeException("Invalid action");
        }

//        participant.setRespondedAt(LocalDateTime.now());
        participantRepository.save(participant);

        // Notify the employee (organizer/host) of the response
        Employee organizer = participant.getMeeting().getEmployee();
        if (organizer != null && organizer.getEmailId() != null) {

            boolean approved = participant.getStatus() == ParticipantStatus.APPROVED;
            String statusLabel = approved ? "Pass Generated" : "Rejected";
            String statusColor = approved ? "#16a34a" : "#dc2626";
            String message = approved
                    ? participant.getParticipantName() + " has confirmed attendance and generated a pass for \""
                        + participant.getMeeting().getMeetingTitle() + "\"."
                    : participant.getParticipantName() + " has rejected the meeting \""
                        + participant.getMeeting().getMeetingTitle() + "\".";

            resendEmailService.sendMeetingStatusEmail(
                    organizer.getEmailId(),
                    organizer.getFirstName(),
                    participant.getParticipantName(),
                    statusLabel,
                    statusColor,
                    participant.getMeeting().getMeetingDate().toString(),
                    participant.getMeeting().getMeetingTime().toString(),
                    message
            );
        }

        return participant.getStatus() == ParticipantStatus.APPROVED
                ? "Pass generated. Thank you for confirming your attendance."
                : "You have declined the meeting";
    }
    
    public ParticipantMeetingDetailsResponse getParticipantDetails(String token) {

        EmployeeMeetingParticipant participant = participantRepository
                .findByApprovalToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired link"));

        Employee organizer = participant.getMeeting().getEmployee();
        String organizerName = organizer.getFirstName();
        if (organizer.getLastName() != null && !organizer.getLastName().trim().isEmpty()) {
            organizerName += " " + organizer.getLastName();
        }

        ParticipantMeetingDetailsResponse res = new ParticipantMeetingDetailsResponse();
        res.setParticipantName(participant.getParticipantName());
        res.setParticipantEmail(participant.getParticipantEmail());
        res.setParticipantMobile(participant.getParticipantMobile());
        res.setOrganizerName(organizerName);
        res.setMeetingTitle(participant.getMeeting().getMeetingTitle());
        res.setMeetingDate(participant.getMeeting().getMeetingDate().toString());
        res.setMeetingTime(participant.getMeeting().getMeetingTime().toString());
        res.setStatus(participant.getStatus().name());
        return res;
    }
}