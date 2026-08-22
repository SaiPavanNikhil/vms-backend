package com.vms_backend.vms_backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vms_backend.vms_backend.dto.EmployeeDashboardStatsResponse;
import com.vms_backend.vms_backend.dto.EmployeeMeetingPassResponse;
import com.vms_backend.vms_backend.dto.EmployeeMeetingRequestDTO;
import com.vms_backend.vms_backend.dto.ParticipantMeetingDetailsResponse;
import com.vms_backend.vms_backend.dto.RecentVisitorResponse;
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

    @Value("${app.frontend-url}")
    private String frontendUrl;


    @Transactional
    public String scheduleMeeting(EmployeeMeetingRequestDTO request) {

        Employee employee = employeeRepository
                .findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (request.getParticipants() == null || request.getParticipants().isEmpty()) {
            throw new RuntimeException("At least one participant is required");
        }

        if (request.getMeetingTitle() == null || request.getMeetingTitle().isBlank()) {
            throw new RuntimeException("Meeting title is required");
        }

        if (request.getMeetingDate() == null) {
            throw new RuntimeException("Meeting date is required");
        }

        if (request.getMeetingTime() == null) {
            throw new RuntimeException("Meeting time is required");
        }

        EmployeeMeeting meeting = EmployeeMeeting.builder()
                .employee(employee)
                .meetingTitle(request.getMeetingTitle().trim())
                .meetingPurpose(request.getMeetingPurpose())
                .venue(request.getVenue())
                .meetingDate(request.getMeetingDate())
                .meetingTime(request.getMeetingTime())
                .createdAt(LocalDateTime.now())
                .build();

        for (EmployeeMeetingRequestDTO.ParticipantDTO participantDTO : request.getParticipants()) {

            if (participantDTO.getName() == null || participantDTO.getName().isBlank()) {
                throw new RuntimeException("Participant name is required");
            }

            if (participantDTO.getEmail() == null || participantDTO.getEmail().isBlank()) {
                throw new RuntimeException("Participant email is required");
            }

            if (participantDTO.getOrganisation() == null || participantDTO.getOrganisation().isBlank()) {
                throw new RuntimeException("Participant organisation is required");
            }

            if (participantDTO.getMobileNo() == null || participantDTO.getMobileNo().isBlank()) {
                throw new RuntimeException("Participant mobile number is required");
            }

            EmployeeMeetingParticipant participant = EmployeeMeetingParticipant.builder()
                    .meeting(meeting)
                    .participantName(participantDTO.getName().trim())
                    .participantEmail(participantDTO.getEmail().trim())
                    .participantMobile(participantDTO.getMobileNo().trim())
                    .participantOrganisation(participantDTO.getOrganisation().trim())
                    .status(ParticipantStatus.PENDING)
                    .build();

            meeting.getParticipants().add(participant);
        }

        employeeMeetingRepository.save(meeting);

        String organizerName = employee.getFirstName();
        if (employee.getLastName() != null && !employee.getLastName().trim().isEmpty()) {
            organizerName += " " + employee.getLastName();
        }
        for (EmployeeMeetingParticipant participant : meeting.getParticipants()) {
        	resendEmailService.sendParticipantInviteEmail(
                    participant.getParticipantEmail(),
                    participant.getParticipantName(),
                    organizerName,
                    employee.getDesignation(),
                    request.getMeetingDate().toString(),
                    request.getMeetingTime().toString(),
                    request.getMeetingPurpose(),
                    request.getVenue(),
                    meeting.getMeetingId().toString(),
                    participant.getParticipantMobile()
            );
        }
//        for (EmployeeMeetingParticipant participant : meeting.getParticipants()) {
//            emailService.sendParticipantInviteEmail(
//                    participant.getParticipantEmail(),
//                    participant.getParticipantName(),
//                    organizerName,
//                    request.getMeetingDate().toString(),
//                    request.getMeetingTime().toString(),
//                    meeting.getMeetingId().toString(),
//                    participant.getParticipantMobile()
//            );
//        }

        return "Meeting scheduled successfully";
    }

    @Transactional
    public String respondToInvitation(String meetingId, String mobileNo, String action) {

        EmployeeMeetingParticipant participant = participantRepository
                .findByMeeting_MeetingIdAndParticipantMobile(Long.parseLong(meetingId), mobileNo)
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

        participantRepository.save(participant);

        Employee organizer = participant.getMeeting().getEmployee();
        String organizerName = organizer != null
                ? organizer.getFirstName()
                    + (organizer.getLastName() != null && !organizer.getLastName().isBlank()
                            ? " " + organizer.getLastName() : "")
                : "";

        String date = participant.getMeeting().getMeetingDate().toString();
        String time = participant.getMeeting().getMeetingTime().toString();

        boolean approved = participant.getStatus() == ParticipantStatus.APPROVED;

        if (approved) {

            String passNo = "PASS-" + date.replace("-", "") + "-" + participant.getParticipantMobile();
            participant.setPassNo(passNo);
            participantRepository.save(participant);

            String passLink = frontendUrl
                    + "/employee-pass?meetingId=" + meetingId      // was "/EmployeeMeetingPass"
                    + "&mobileNo=" + mobileNo;
            if (organizer != null && organizer.getEmailId() != null && !organizer.getEmailId().isBlank()) {
            	resendEmailService.sendVisitorStatusEmail(
                        organizer.getEmailId(),
                        organizerName,
                        participant.getParticipantName(),
                        "Pass Generated",
                        "#16a34a",
                        date,
                        time,
                        participant.getParticipantName() + " has confirmed attendance and generated a pass for \""
                                + participant.getMeeting().getMeetingTitle() + "\".",
                        passLink
                );
            }

            resendEmailService.sendHostApprovedEmail(
                    participant.getParticipantEmail(),
                    organizerName,
                    participant.getParticipantName(),
                    date,
                    time,
                    passNo,
                    passLink
            );

        } else {

            if (organizer != null && organizer.getEmailId() != null && !organizer.getEmailId().isBlank()) {
            	resendEmailService.sendVisitorStatusEmail(
                        organizer.getEmailId(),
                        organizerName,
                        participant.getParticipantName(),
                        "Rejected",
                        "#dc2626",
                        date,
                        time,
                        participant.getParticipantName() + " has rejected the meeting \""
                                + participant.getMeeting().getMeetingTitle() + "\".",
                        null
                );
            }
        }

        return approved
                ? "Pass generated. Thank you for confirming your attendance."
                : "You have declined the meeting";
    }

    public ParticipantMeetingDetailsResponse getParticipantDetails(String meetingId, String mobileNo) {

        EmployeeMeetingParticipant participant = participantRepository
                .findByMeeting_MeetingIdAndParticipantMobile(Long.parseLong(meetingId), mobileNo)
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
    public EmployeeMeetingPassResponse getMeetingPass(String meetingId, String mobileNo) {

        EmployeeMeetingParticipant participant = participantRepository
                .findByMeeting_MeetingIdAndParticipantMobile(Long.parseLong(meetingId), mobileNo)
                .orElseThrow(() -> new RuntimeException("Pass not found"));

        if (participant.getStatus() != ParticipantStatus.APPROVED) {
            throw new RuntimeException("Meeting is not approved yet");
        }

        Employee organizer = participant.getMeeting().getEmployee();
        String hostName = organizer.getFirstName();
        if (organizer.getLastName() != null && !organizer.getLastName().trim().isEmpty()) {
            hostName += " " + organizer.getLastName();
        }

        return EmployeeMeetingPassResponse.builder()
                .meetingId(meetingId)
                .passNo(participant.getPassNo())
                .participantName(participant.getParticipantName())
                .participantOrganisation(participant.getParticipantOrganisation())
                .mobileNo(participant.getParticipantMobile())
                .meetingTitle(participant.getMeeting().getMeetingTitle())
                .meetingDate(participant.getMeeting().getMeetingDate().toString())
                .meetingTime(participant.getMeeting().getMeetingTime().toString())
                .hostName(hostName)
                .build();
    }
    //new
 // add this method inside the class
    public EmployeeDashboardStatsResponse getDashboardStats(String employeeId) {

        if (employeeId == null || employeeId.isBlank()) {
            throw new RuntimeException("Employee id is required");
        }

        LocalDate today = LocalDate.now();

        long appointmentsToday = employeeMeetingRepository
                .countByEmployee_EmployeeIdAndMeetingDate(employeeId, today);

        long todaysVisitors = participantRepository
                .countTodaysApprovedVisitors(employeeId, today);

        long activePasses = participantRepository
                .countActivePassesToday(employeeId, today);

        long pendingRequests = participantRepository
                .countByMeeting_Employee_EmployeeIdAndStatus(employeeId, ParticipantStatus.PENDING);

        return EmployeeDashboardStatsResponse.builder()
                .todaysVisitors(todaysVisitors)
                .appointmentsToday(appointmentsToday)
                .activePasses(activePasses)
                .pendingRequests(pendingRequests)
                .build();
    }
    //THIS CODE FOR SEND HOST EMPLOYEE SEND INVITATION STATUS DETAILS PERSOANL RECENT VISITOR DETAILS EMPLYEE DASHBAORD
 // add this method inside the class
    public List<RecentVisitorResponse> getRecentVisitors(String employeeId, int limit) {

        if (employeeId == null || employeeId.isBlank()) {
            throw new RuntimeException("Employee id is required");
        }

        List<EmployeeMeetingParticipant> participants = participantRepository
                .findRecentByEmployeeRaw(employeeId)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        return participants.stream()
                .map(p -> {

                    Employee organizer = p.getMeeting().getEmployee();

                    String hostName = organizer != null
                            ? organizer.getFirstName()
                                + (organizer.getLastName() != null && !organizer.getLastName().isBlank()
                                    ? " " + organizer.getLastName() : "")
                            : "";

                    String statusLabel = switch (p.getStatus()) {
                        case APPROVED -> "Approved";
                        case REJECTED -> "Rejected";
                        default -> "Scheduled";
                    };

                    String formattedTime = p.getMeeting().getMeetingTime() != null
                            ? p.getMeeting().getMeetingTime().format(timeFormatter)
                            : "";

                    return RecentVisitorResponse.builder()
                            .visitorName(p.getParticipantName())
                            .purpose(p.getMeeting().getMeetingPurpose())
                            .hostName(hostName)
                            .time(formattedTime)
                            .status(statusLabel)
                            .build();
                })
                .collect(Collectors.toList());
    }
}