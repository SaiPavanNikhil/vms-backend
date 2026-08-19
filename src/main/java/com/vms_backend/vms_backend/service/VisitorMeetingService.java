package com.vms_backend.vms_backend.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vms_backend.vms_backend.dto.VisitorMeetingApprovalRequest;
import com.vms_backend.vms_backend.dto.VisitorMeetingRequest;
import com.vms_backend.vms_backend.dto.VisitorMeetingResponse;
import com.vms_backend.vms_backend.entity.Employee;
import com.vms_backend.vms_backend.entity.VisitorMeeting;
import com.vms_backend.vms_backend.entity.Visitors;
import com.vms_backend.vms_backend.repository.EmployeeRepository;
import com.vms_backend.vms_backend.repository.VisitorMeetingRepository;
import com.vms_backend.vms_backend.repository.VisitorRepository;

@Service
public class VisitorMeetingService {

    private final VisitorMeetingRepository meetingRepo;
    private final VisitorRepository visitorRepo;
    private final EmployeeRepository employeeRepo;
//    private final EmailService emailService;
    private final ResendEmailService resendEmailService;
    
    @Autowired
    private EncryptionService encryptionService;
    
    @Value("${app.frontend-url}")
    private String frontendUrl;

    public VisitorMeetingService(VisitorMeetingRepository meetingRepo,
                                  VisitorRepository visitorRepo,
                                  EmployeeRepository employeeRepo,
                                  ResendEmailService resendEmailService) {
        this.meetingRepo = meetingRepo;
        this.visitorRepo = visitorRepo;
        this.employeeRepo = employeeRepo;
        this.resendEmailService = resendEmailService;
    }

    @Transactional
    public VisitorMeeting createRequest(VisitorMeetingRequest req) throws Exception {
        VisitorMeeting m = new VisitorMeeting();
        m.setMobileNo(req.getMobileNo());
        m.setHostId(req.getHostId());
        m.setRequestedMeetingDate(req.getRequestedMeetingDate());
        m.setRequestedMeetingTime(req.getRequestedMeetingTime());
        m.setAcceptFlag("N");
        VisitorMeeting saved = meetingRepo.save(m);

        notifyHost(saved);

        return saved;
    }

    // Newest-first: meetingId is auto-increment, so ordering by it descending
    // gives the most recently created request first — this is what drives
    // "show the latest data" when the Angular page opens with ?hostId=&mobileNo=.
    public List<VisitorMeetingResponse> getRequestsForHost(String hostId) {
        return meetingRepo.findByHostIdOrderByMeetingIdDesc(hostId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<VisitorMeetingResponse> getAllRequests() {
        return meetingRepo.findAllByOrderByMeetingIdDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Used once hostId+mobileNo are both known (e.g. after decoding a token).
    public Optional<VisitorMeetingResponse> getLatestRequest(String hostId, String mobileNo) {
        return meetingRepo.findFirstByHostIdAndMobileNoOrderByMeetingIdDesc(hostId, mobileNo)
                .map(this::toResponse);
    }

    // Used for a bare ?hostId=EMP001 link — finds that host's single most
    // recent request across all visitors, so the frontend can redirect into
    // the encrypted hostId+mobileNo token for that specific record.
    public Optional<VisitorMeetingResponse> getLatestRequestForHost(String hostId) {
        return meetingRepo.findFirstByHostIdOrderByMeetingIdDesc(hostId)
                .map(this::toResponse);
    }

//    @Transactional
//    public VisitorMeeting approve(Integer meetingId, VisitorMeetingApprovalRequest req) {
//        VisitorMeeting m = meetingRepo.findById(meetingId)
//                .orElseThrow(() -> new NoSuchElementException("Meeting not found: " + meetingId));
//        m.setAcceptFlag("Y");
//        m.setApprovedMeetingDate(req.getApprovedMeetingDate() != null ? req.getApprovedMeetingDate() : m.getRequestedMeetingDate());
//        m.setApprovedMeetingTime(req.getApprovedMeetingTime() != null ? req.getApprovedMeetingTime() : m.getRequestedMeetingTime());
//        VisitorMeeting saved = meetingRepo.save(m);
//
//        notifyVisitor(saved, "Approved", "#16a34a",
//                "Your meeting request has been approved. Please arrive on time and carry a valid ID for verification.");
//
//        return saved;
//    }
    
    @Transactional
    public VisitorMeeting approve(
            Integer meetingId,
            VisitorMeetingApprovalRequest req) {

        VisitorMeeting m = meetingRepo.findById(meetingId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Meeting not found: " + meetingId
                        )
                );


        // =====================================================
        // APPROVE MEETING
        // =====================================================

        m.setAcceptFlag("Y");


        // =====================================================
        // APPROVED DATE
        // =====================================================

        LocalDate approvedDate =
                req.getApprovedMeetingDate() != null
                        ? req.getApprovedMeetingDate()
                        : m.getRequestedMeetingDate();

        m.setApprovedMeetingDate(approvedDate);


        // =====================================================
        // APPROVED TIME
        // =====================================================

        LocalTime approvedTime =
                req.getApprovedMeetingTime() != null
                        ? req.getApprovedMeetingTime()
                        : m.getRequestedMeetingTime();

        m.setApprovedMeetingTime(approvedTime);


        // =====================================================
        // GENERATE PASS NUMBER
        // =====================================================

        Integer maxSequence =
                meetingRepo.findMaxPassSequenceByDate(
                        approvedDate
                );

        int nextSequence =
                (maxSequence == null ? 0 : maxSequence) + 1;


        String passNo = String.format(
                "PASS-%s-%03d",
                approvedDate.format(
                        DateTimeFormatter.BASIC_ISO_DATE
                ),
                nextSequence
        );


        m.setPassNo(passNo);


        // =====================================================
        // SAVE
        // =====================================================

        VisitorMeeting saved =
                meetingRepo.save(m);


        // =====================================================
        // VISITOR PASS LINK
        // =====================================================
        
        String payload = saved.getMeetingId().toString();
        String encryptedToken;

        try {
            encryptedToken = encryptionService.encrypt(payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt visitor pass token", e);
        }

//
//        String passLink =
//        		frontendUrl+"/visitor-pass/"
//                + saved.getMeetingId();
        
        String passLink =
                frontendUrl
                + "/visitor-pass"
                + URLEncoder.encode(
                        encryptedToken,
                        StandardCharsets.UTF_8
                );


        // =====================================================
        // NOTIFY VISITOR
        // =====================================================

//        notifyVisitor(
//                saved,
//                "Approved",
//                "#16a34a",
//                "Your meeting request has been approved."
//                        + "\n\n"
//                        + "Visitor Pass No: "
//                        + saved.getPassNo()
//                        + "\n\n"
//                        + "Please open your visitor pass:"
//                        + "\n"
//                        + passLink
//                        + "\n\n"
//                        + "Please arrive on time and carry a valid ID for verification."
//        );
        notifyVisitor(
                saved,
                "Approved",
                "#16a34a",
                "Your meeting request has been approved."
                        + "\n\n"
                        + "Visitor Pass No: "
                        + saved.getPassNo()
                        + "\n\n"
                        + "Please open your visitor pass:"
                        + "\n"
                        + passLink
                        + "\n\n"
                        + "Please arrive on time and carry a valid ID for verification.",
                passLink
        );
        
        notifyHostApproved(saved, passLink);

        return saved;
    }

    @Transactional
    public VisitorMeeting reject(Integer meetingId) {
        VisitorMeeting m = meetingRepo.findById(meetingId)
                .orElseThrow(() -> new NoSuchElementException("Meeting not found: " + meetingId));
        m.setAcceptFlag("R");
        VisitorMeeting saved = meetingRepo.save(m);

//        notifyVisitor(saved, "Rejected", "#dc2626",
//                "We're sorry, but your meeting request could not be accommodated at this time.");
        notifyVisitor(saved, "Rejected", "#dc2626",
                "We're sorry, but your meeting request could not be accommodated at this time.",
                null);

        return saved;
    }

    @Transactional
    public VisitorMeeting hold(Integer meetingId, VisitorMeetingApprovalRequest req) {
        VisitorMeeting m = meetingRepo.findById(meetingId)
                .orElseThrow(() -> new NoSuchElementException("Meeting not found: " + meetingId));
        m.setAcceptFlag("H");
        // Store the rescheduled slot the host is proposing; falls back to the originally requested slot if not sent
        m.setApprovedMeetingDate(req.getApprovedMeetingDate() != null ? req.getApprovedMeetingDate() : m.getRequestedMeetingDate());
        m.setApprovedMeetingTime(req.getApprovedMeetingTime() != null ? req.getApprovedMeetingTime() : m.getRequestedMeetingTime());
        VisitorMeeting saved = meetingRepo.save(m);

//        notifyVisitor(saved, "On Hold", "#d97706",
//                "Your host has put your meeting on hold and proposed a new time. You will be notified once this is confirmed.");
        notifyVisitor(saved, "On Hold", "#d97706",
                "Your host has put your meeting on hold and proposed a new time. You will be notified once this is confirmed.",
                null);

        return saved;
    }

    // Looks up the host's email and visitor's name, then sends the approval-request
    // email with an encoded token embedding hostId+mobileNo. Logs (instead of silently
    // skipping) when the host or visitor record can't be found, or has no email on file —
    // this makes a "why isn't the email sending" case show up in the console.
    
    private void notifyHost(VisitorMeeting m) throws Exception {

        Employee host = employeeRepo.findById(m.getHostId()).orElse(null);

        if (host == null) {
            System.err.println(
                    "notifyHost: no Employee found for hostId=" + m.getHostId()
            );
            return;
        }

        if (host.getEmailId() == null || host.getEmailId().isBlank()) {
            System.err.println(
                    "notifyHost: host " + m.getHostId()
                            + " has no emailId on file"
            );
            return;
        }

        Visitors v = visitorRepo.findById(m.getMobileNo()).orElse(null);

        if (v == null) {
            System.err.println(
                    "notifyHost: no Visitors row found for mobileNo="
                            + m.getMobileNo()
            );
            return;
        }

        String visitorName =
                (v.getFirstName() + " "
                        + (v.getLastName() != null
                                ? v.getLastName()
                                : ""))
                        .trim();

        String hostName =
                (host.getFirstName() + " "
                        + (host.getLastName() != null
                                ? host.getLastName()
                                : ""))
                        .trim();

        String registeredDate =
                String.valueOf(m.getRequestedMeetingDate());

        resendEmailService.sendHostApprovalEmail(
                host.getEmailId(),
                visitorName,
                hostName,
                registeredDate,
                String.valueOf(m.getRequestedMeetingDate()),
                String.valueOf(m.getRequestedMeetingTime()),
                m.getHostId(),
                m.getMobileNo()
        );
    }
    
    private void notifyHostApproved(
        VisitorMeeting m,
        String passLink) {

    Employee host =
            employeeRepo.findById(m.getHostId()).orElse(null);

    if (host == null) {
        System.err.println(
                "notifyHostApproved: no Employee found for hostId="
                        + m.getHostId()
        );
        return;
    }

    if (host.getEmailId() == null || host.getEmailId().isBlank()) {
        System.err.println(
                "notifyHostApproved: host "
                        + m.getHostId()
                        + " has no emailId on file"
        );
        return;
    }

    Visitors v =
            visitorRepo.findById(m.getMobileNo()).orElse(null);

    if (v == null) {
        System.err.println(
                "notifyHostApproved: no Visitors row found for mobileNo="
                        + m.getMobileNo()
        );
        return;
    }

    String visitorName =
            (v.getFirstName() + " "
                    + (v.getLastName() != null
                            ? v.getLastName()
                            : ""))
                    .trim();

    String hostName =
            (host.getFirstName() + " "
                    + (host.getLastName() != null
                            ? host.getLastName()
                            : ""))
                    .trim();

    resendEmailService.sendHostApprovedEmail(
            host.getEmailId(),
            visitorName,
            hostName,
            String.valueOf(m.getApprovedMeetingDate()),
            String.valueOf(m.getApprovedMeetingTime()),
            m.getPassNo(),
            passLink
    );
}

    // Looks up the visitor's email and host name, then sends the styled card notification.
    // Logs (instead of silently skipping) when the visitor record can't be found, or has
    // no email on file — this is the "why didn't the visitor get the Approved email" fix.
    private void notifyVisitor(VisitorMeeting m, String statusLabel, String statusColor, String message, String passLink) {
        Visitors v = visitorRepo.findById(m.getMobileNo()).orElse(null);
        if (v == null) {
            System.err.println("notifyVisitor: no Visitors row found for mobileNo=" + m.getMobileNo());
            return;
        }
        if (v.getEmail() == null || v.getEmail().isBlank()) {
            System.err.println("notifyVisitor: visitor " + m.getMobileNo() + " has no email on file");
            return;
        }

        String visitorName = (v.getFirstName() + " " + (v.getLastName() != null ? v.getLastName() : "")).trim();
        String hostName = employeeRepo.findById(m.getHostId())
                .map(e -> (e.getFirstName() + " " + (e.getLastName() != null ? e.getLastName() : "")).trim())
                .orElse("your host");

        resendEmailService.sendVisitorStatusEmail(
                v.getEmail(), visitorName, hostName, statusLabel, statusColor,
                String.valueOf(m.getApprovedMeetingDate() != null ? m.getApprovedMeetingDate() : m.getRequestedMeetingDate()),
                String.valueOf(m.getApprovedMeetingTime() != null ? m.getApprovedMeetingTime() : m.getRequestedMeetingTime()),
                message,
                passLink
        );
    }
    private VisitorMeetingResponse toResponse(VisitorMeeting m) {
        VisitorMeetingResponse r = new VisitorMeetingResponse();
        r.setMeetingId(m.getMeetingId());
        r.setMobileNo(m.getMobileNo());
        r.setHostId(m.getHostId());
        r.setRequestedMeetingDate(m.getRequestedMeetingDate());
        r.setRequestedMeetingTime(m.getRequestedMeetingTime());
        r.setAcceptFlag(m.getAcceptFlag());
        r.setApprovedMeetingDate(m.getApprovedMeetingDate());
        r.setApprovedMeetingTime(m.getApprovedMeetingTime());

        visitorRepo.findById(m.getMobileNo()).ifPresent((Visitors v) ->
                r.setVisitorName((v.getFirstName() + " " + (v.getLastName() != null ? v.getLastName() : "")).trim())
        );

        employeeRepo.findById(m.getHostId()).ifPresent((Employee e) ->
                r.setHostName((e.getFirstName() + " " + (e.getLastName() != null ? e.getLastName() : "")).trim())
        );

        return r;
    }
}
//package com.vms_backend.vms_backend.service;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.vms_backend.vms_backend.dto.VisitorMeetingApprovalRequest;
//import com.vms_backend.vms_backend.dto.VisitorMeetingRequest;
//import com.vms_backend.vms_backend.dto.VisitorMeetingResponse;
//import com.vms_backend.vms_backend.entity.Employee;
//import com.vms_backend.vms_backend.entity.VisitorMeeting;
//import com.vms_backend.vms_backend.entity.Visitors;
//import com.vms_backend.vms_backend.repository.EmployeeRepository;
//import com.vms_backend.vms_backend.repository.VisitorMeetingRepository;
//import com.vms_backend.vms_backend.repository.VisitorRepository;
//import com.vms_backend.vms_backend.util.TokenUtil;
//
//@Service
//public class VisitorMeetingService {
//
//    private final VisitorMeetingRepository meetingRepo;
//    private final VisitorRepository visitorRepo;
//    private final EmployeeRepository employeeRepo;
//    private final EmailService emailService;
//
//    public VisitorMeetingService(VisitorMeetingRepository meetingRepo,
//                                  VisitorRepository visitorRepo,
//                                  EmployeeRepository employeeRepo,
//                                  EmailService emailService) {
//        this.meetingRepo = meetingRepo;
//        this.visitorRepo = visitorRepo;
//        this.employeeRepo = employeeRepo;
//        this.emailService = emailService;
//    }
//
//    @Transactional
//    public VisitorMeeting createRequest(VisitorMeetingRequest req) {
//        VisitorMeeting m = new VisitorMeeting();
//        m.setMobileNo(req.getMobileNo());
//        m.setHostId(req.getHostId());
//        m.setRequestedMeetingDate(req.getRequestedMeetingDate());
//        m.setRequestedMeetingTime(req.getRequestedMeetingTime());
//        m.setAcceptFlag("N");
//        VisitorMeeting saved = meetingRepo.save(m);
//
//        notifyHost(saved);
//
//        return saved;
//    }
//
//    // Newest-first: meetingId is auto-increment, so ordering by it descending
//    // gives the most recently created request first — this is what drives
//    // "show the latest data" when the Angular page opens with ?hostId=&mobileNo=.
//    public List<VisitorMeetingResponse> getRequestsForHost(String hostId) {
//        return meetingRepo.findByHostIdOrderByMeetingIdDesc(hostId).stream()
//                .map(this::toResponse)
//                .collect(Collectors.toList());
//    }
//
//    public List<VisitorMeetingResponse> getAllRequests() {
//        return meetingRepo.findAllByOrderByMeetingIdDesc().stream()
//                .map(this::toResponse)
//                .collect(Collectors.toList());
//    }
//
//    // Used once hostId+mobileNo are both known (e.g. after decoding a token).
//    public Optional<VisitorMeetingResponse> getLatestRequest(String hostId, String mobileNo) {
//        return meetingRepo.findFirstByHostIdAndMobileNoOrderByMeetingIdDesc(hostId, mobileNo)
//                .map(this::toResponse);
//    }
//
//    // Used for a bare ?hostId=EMP001 link — finds that host's single most
//    // recent request across all visitors, so the frontend can redirect into
//    // the encrypted hostId+mobileNo token for that specific record.
//    public Optional<VisitorMeetingResponse> getLatestRequestForHost(String hostId) {
//        return meetingRepo.findFirstByHostIdOrderByMeetingIdDesc(hostId)
//                .map(this::toResponse);
//    }
//
////    @Transactional
////    public VisitorMeeting approve(Integer meetingId, VisitorMeetingApprovalRequest req) {
////        VisitorMeeting m = meetingRepo.findById(meetingId)
////                .orElseThrow(() -> new NoSuchElementException("Meeting not found: " + meetingId));
////        m.setAcceptFlag("Y");
////        m.setApprovedMeetingDate(req.getApprovedMeetingDate() != null ? req.getApprovedMeetingDate() : m.getRequestedMeetingDate());
////        m.setApprovedMeetingTime(req.getApprovedMeetingTime() != null ? req.getApprovedMeetingTime() : m.getRequestedMeetingTime());
////        VisitorMeeting saved = meetingRepo.save(m);
////
////        notifyVisitor(saved, "Approved", "#16a34a",
////                "Your meeting request has been approved. Please arrive on time and carry a valid ID for verification.");
////
////        return saved;
////    }
//
//    @Transactional
//    public VisitorMeeting approve(
//            Integer meetingId,
//            VisitorMeetingApprovalRequest req) {
//
//        VisitorMeeting m = meetingRepo.findById(meetingId)
//                .orElseThrow(() ->
//                        new NoSuchElementException(
//                                "Meeting not found: " + meetingId
//                        )
//                );
//
//
//        // =====================================================
//        // APPROVE MEETING
//        // =====================================================
//
//        m.setAcceptFlag("Y");
//
//
//        // =====================================================
//        // APPROVED DATE
//        // =====================================================
//
//        LocalDate approvedDate =
//                req.getApprovedMeetingDate() != null
//                        ? req.getApprovedMeetingDate()
//                        : m.getRequestedMeetingDate();
//
//        m.setApprovedMeetingDate(approvedDate);
//
//
//        // =====================================================
//        // APPROVED TIME
//        // =====================================================
//
//        LocalTime approvedTime =
//                req.getApprovedMeetingTime() != null
//                        ? req.getApprovedMeetingTime()
//                        : m.getRequestedMeetingTime();
//
//        m.setApprovedMeetingTime(approvedTime);
//
//
//        // =====================================================
//        // GENERATE PASS NUMBER
//        // =====================================================
//
//        Integer maxSequence =
//                meetingRepo.findMaxPassSequenceByDate(
//                        approvedDate
//                );
//
//        int nextSequence =
//                (maxSequence == null ? 0 : maxSequence) + 1;
//
//
//        String passNo = String.format(
//                "PASS-%s-%03d",
//                approvedDate.format(
//                        DateTimeFormatter.BASIC_ISO_DATE
//                ),
//                nextSequence
//        );
//
//
//        m.setPassNo(passNo);
//
//
//        // =====================================================
//        // SAVE
//        // =====================================================
//
//        VisitorMeeting saved =
//                meetingRepo.save(m);
//
//
//        // =====================================================
//        // VISITOR PASS LINK
//        // =====================================================
//
//        String passLink =
//                "http://localhost:4200/visitor-pass/"
//                + saved.getMeetingId();
//
//
//        // =====================================================
//        // NOTIFY VISITOR
//        // =====================================================
//
//        notifyVisitor(
//                saved,
//                "Approved",
//                "#16a34a",
//                "Your meeting request has been approved."
//                        + "\n\n"
//                        + "Visitor Pass No: "
//                        + saved.getPassNo()
//                        + "\n\n"
//                        + "Please open your visitor pass:"
//                        + "\n"
//                        + passLink
//                        + "\n\n"
//                        + "Please arrive on time and carry a valid ID for verification."
//        );
//
//
//        return saved;
//    }
//    
//    @Transactional
//    public VisitorMeeting reject(Integer meetingId) {
//        VisitorMeeting m = meetingRepo.findById(meetingId)
//                .orElseThrow(() -> new NoSuchElementException("Meeting not found: " + meetingId));
//        m.setAcceptFlag("R");
//        VisitorMeeting saved = meetingRepo.save(m);
//
//        notifyVisitor(saved, "Rejected", "#dc2626",
//                "We're sorry, but your meeting request could not be accommodated at this time.");
//
//        return saved;
//    }
//
//    @Transactional
//    public VisitorMeeting hold(Integer meetingId, VisitorMeetingApprovalRequest req) {
//        VisitorMeeting m = meetingRepo.findById(meetingId)
//                .orElseThrow(() -> new NoSuchElementException("Meeting not found: " + meetingId));
//        m.setAcceptFlag("H");
//        // Store the rescheduled slot the host is proposing; falls back to the originally requested slot if not sent
//        m.setApprovedMeetingDate(req.getApprovedMeetingDate() != null ? req.getApprovedMeetingDate() : m.getRequestedMeetingDate());
//        m.setApprovedMeetingTime(req.getApprovedMeetingTime() != null ? req.getApprovedMeetingTime() : m.getRequestedMeetingTime());
//        VisitorMeeting saved = meetingRepo.save(m);
//
//        notifyVisitor(saved, "On Hold", "#d97706",
//                "Your host has put your meeting on hold and proposed a new time. You will be notified once this is confirmed.");
//
//        return saved;
//    }
//
//    // Looks up the host's email and visitor's name, then sends the approval-request
//    // email with an encoded token embedding hostId+mobileNo. Safe no-op if the host
//    // has no email on file.
//    // Looks up the host's email and visitor's name, then sends the approval-request
//    // email with an encoded token embedding hostId+mobileNo. Safe no-op if the host
//    // has no email on file.
//    private void notifyHost(VisitorMeeting m) {
//        employeeRepo.findById(m.getHostId()).ifPresent((Employee host) -> {
//            if (host.getEmailId() == null || host.getEmailId().isBlank()) return;
//
//            visitorRepo.findById(m.getMobileNo()).ifPresent((Visitors v) -> {
//                String visitorName = (v.getFirstName() + " " + (v.getLastName() != null ? v.getLastName() : "")).trim();
//                String hostName = (host.getFirstName() + " " + (host.getLastName() != null ? host.getLastName() : "")).trim();
//
//                String registeredDate = String.valueOf(m.getRequestedMeetingDate());
//
//                emailService.sendHostApprovalEmail(
//                        host.getEmailId(), visitorName, hostName, registeredDate,
//                        String.valueOf(m.getRequestedMeetingDate()),
//                        String.valueOf(m.getRequestedMeetingTime()),
//                        m.getHostId(), m.getMobileNo()
//                );
//            });
//        });
//    }
//    // Looks up the visitor's email and host name, then sends the styled card notification.
//    // Safe no-op if the visitor has no email on file.
//    private void notifyVisitor(VisitorMeeting m, String statusLabel, String statusColor, String message) {
//        visitorRepo.findById(m.getMobileNo()).ifPresent((Visitors v) -> {
//            if (v.getEmail() == null || v.getEmail().isBlank()) return;
//
//            String visitorName = (v.getFirstName() + " " + (v.getLastName() != null ? v.getLastName() : "")).trim();
//            String hostName = employeeRepo.findById(m.getHostId())
//                    .map(e -> (e.getFirstName() + " " + (e.getLastName() != null ? e.getLastName() : "")).trim())
//                    .orElse("your host");
//
//            String token = TokenUtil.encode(m.getHostId(), m.getMobileNo());
//
//            emailService.sendVisitorStatusEmail(
//                    v.getEmail(), visitorName, hostName, statusLabel, statusColor,
//                    String.valueOf(m.getApprovedMeetingDate() != null ? m.getApprovedMeetingDate() : m.getRequestedMeetingDate()),
//                    String.valueOf(m.getApprovedMeetingTime() != null ? m.getApprovedMeetingTime() : m.getRequestedMeetingTime()),
//                    message,
//                    token
//            );
//        });
//    }
//    private VisitorMeetingResponse toResponse(VisitorMeeting m) {
//        VisitorMeetingResponse r = new VisitorMeetingResponse();
//        r.setMeetingId(m.getMeetingId());
//        r.setMobileNo(m.getMobileNo());
//        r.setHostId(m.getHostId());
//        r.setRequestedMeetingDate(m.getRequestedMeetingDate());
//        r.setRequestedMeetingTime(m.getRequestedMeetingTime());
//        r.setAcceptFlag(m.getAcceptFlag());
//        r.setApprovedMeetingDate(m.getApprovedMeetingDate());
//        r.setApprovedMeetingTime(m.getApprovedMeetingTime());
//
//        visitorRepo.findById(m.getMobileNo()).ifPresent((Visitors v) ->
//                r.setVisitorName((v.getFirstName() + " " + (v.getLastName() != null ? v.getLastName() : "")).trim())
//        );
//
//        employeeRepo.findById(m.getHostId()).ifPresent((Employee e) ->
//                r.setHostName((e.getFirstName() + " " + (e.getLastName() != null ? e.getLastName() : "")).trim())
//        );
//
//        return r;
//    }
//}
