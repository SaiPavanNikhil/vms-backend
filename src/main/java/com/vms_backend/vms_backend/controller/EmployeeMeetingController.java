package com.vms_backend.vms_backend.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vms_backend.vms_backend.dto.EmployeeMeetingRequestDTO;
import com.vms_backend.vms_backend.dto.ParticipantResponseDTO;
import com.vms_backend.vms_backend.entity.EmployeeMeetingParticipant;
import com.vms_backend.vms_backend.repository.EmployeeMeetingParticipantRepository;
import com.vms_backend.vms_backend.service.EmployeeMeetingService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employee-meetings")
@RequiredArgsConstructor
public class EmployeeMeetingController {

    private final EmployeeMeetingService employeeMeetingService;
    private final EmployeeMeetingParticipantRepository participantRepository;

    @PostMapping
    public ResponseEntity<MeetingResponse> scheduleMeeting(
            @RequestBody EmployeeMeetingRequestDTO request) {

        try {
            String message = employeeMeetingService.scheduleMeeting(request);
            return ResponseEntity.ok(new MeetingResponse(true, message));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MeetingResponse(false, e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MeetingResponse(false, "Unable to schedule meeting"));
        }
    }

    // Called by the participant-response Angular page
    @PostMapping("/participant-response")
    public ResponseEntity<MeetingResponse> respondToInvitation(
            @RequestBody ParticipantResponseDTO request) {

        try {
            String message = employeeMeetingService
                    .respondToInvitation(request.getMeetingId(), request.getMobileNo(), request.getAction());

            return ResponseEntity.ok(new MeetingResponse(true, message));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MeetingResponse(false, e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MeetingResponse(false, "Unable to process your response"));
        }
    }

    // Called by the participant-response Angular page to load the details card
    @GetMapping("/participant-response")
    public ResponseEntity<?> getParticipantDetails(
            @RequestParam String meetingId,
            @RequestParam String mobileNo) {
        try {
            return ResponseEntity.ok(employeeMeetingService.getParticipantDetails(meetingId, mobileNo));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MeetingResponse(false, e.getMessage()));
        }
    }
    @GetMapping("/pass")
    public ResponseEntity<?> getMeetingPass(
            @RequestParam String meetingId,
            @RequestParam String mobileNo) {
        try {
            return ResponseEntity.ok(employeeMeetingService.getMeetingPass(meetingId, mobileNo));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MeetingResponse(false, e.getMessage()));
        }
    }
    //new
    @GetMapping("/dashboard-stats")
    public ResponseEntity<?> getDashboardStats(@RequestParam String employeeId) {
        try {
            return ResponseEntity.ok(employeeMeetingService.getDashboardStats(employeeId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MeetingResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MeetingResponse(false, "Unable to load dashboard stats"));
        }
    }
   // THIS CODE FOR SEND HOST EMPLOYEE SEND INVITATION STATUS DETAILS PERSOANL RECENT VISITOR DETAILS EMPLYEE DASHBAORD
    @GetMapping("/recent-visitors")
    public ResponseEntity<?> getRecentVisitors(
            @RequestParam String employeeId,
            @RequestParam(defaultValue = "4") int limit) {
        try {
            return ResponseEntity.ok(employeeMeetingService.getRecentVisitors(employeeId, limit));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MeetingResponse(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MeetingResponse(false, "Unable to load recent visitors"));
        }
    }
    //NEW EMPLYEE DASHBAORD
    @GetMapping("/participant-response/action")
    public ResponseEntity<String> respondViaEmail(
            @RequestParam String meetingId,
            @RequestParam String mobileNo,
            @RequestParam String action) {

        String html;
        try {
            String message = employeeMeetingService.respondToInvitation(meetingId, mobileNo, action);
            html = """
                <html><body style="font-family:'Segoe UI',Arial,sans-serif; text-align:center; padding:60px;">
                  <h2 style="color:#16a34a;">%s</h2>
                </body></html>
                """.formatted(message);
            return ResponseEntity.ok().header("Content-Type", "text/html").body(html);

        } catch (RuntimeException e) {
            html = """
                <html><body style="font-family:'Segoe UI',Arial,sans-serif; text-align:center; padding:60px;">
                  <h2 style="color:#dc2626;">%s</h2>
                </body></html>
                """.formatted(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Content-Type", "text/html").body(html);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MeetingResponse {
        private boolean success;
        private String message;
    }
    
    //new code for photo
    // Serves a participant's photo, mirroring VisitorPassController#getVisitorPhoto
    @GetMapping("/photo/{meetingId}/{mobileNo}")
    public ResponseEntity<?> getParticipantPhoto(
            @PathVariable Long meetingId,
            @PathVariable String mobileNo) {
        try {
            EmployeeMeetingParticipant participant =
                    participantRepository.findByMeeting_MeetingIdAndParticipantMobile(meetingId, mobileNo)
                            .orElseThrow(() -> new RuntimeException("Participant not found"));

            String photoPath = participant.getPhoto();
            if (photoPath == null || photoPath.isBlank()) {
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(photoPath);
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(path);
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            ByteArrayResource resource = new ByteArrayResource(imageBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + path.getFileName() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(imageBytes.length)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Unable to load participant photo"));
        }
    }
    
}
//package com.vms_backend.vms_backend.controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import com.vms_backend.vms_backend.dto.EmployeeMeetingRequestDTO;
//import com.vms_backend.vms_backend.dto.ParticipantResponseDTO;
//import com.vms_backend.vms_backend.service.EmployeeMeetingService;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/api/employee-meetings")
//@RequiredArgsConstructor
//public class EmployeeMeetingController {
//
//    private final EmployeeMeetingService employeeMeetingService;
//
//    @PostMapping
//    public ResponseEntity<MeetingResponse> scheduleMeeting(
//            @RequestBody EmployeeMeetingRequestDTO request) {
//
//        try {
//            String message = employeeMeetingService.scheduleMeeting(request);
//            return ResponseEntity.ok(new MeetingResponse(true, message));
//
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(new MeetingResponse(false, e.getMessage()));
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new MeetingResponse(false, "Unable to schedule meeting"));
//        }
//    }
//
//
//    // Called by the participant-response Angular page
//    @PostMapping("/participant-response")
//    public ResponseEntity<MeetingResponse> respondToInvitation(
//            @RequestBody ParticipantResponseDTO request) {
//
//        try {
//            String message = employeeMeetingService
//                    .respondToInvitation(request.getToken(), request.getAction());
//
//            return ResponseEntity.ok(new MeetingResponse(true, message));
//
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(new MeetingResponse(false, e.getMessage()));
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new MeetingResponse(false, "Unable to process your response"));
//        }
//    }
////  Called by the participant-response Angular page to load the details card
////    @GetMapping("/participant-response/{token}")
////    public ResponseEntity<?> getParticipantDetails(@PathVariable String token) {
////        try {
////            return ResponseEntity.ok(employeeMeetingService.getParticipantDetails(token));
////        } catch (RuntimeException e) {
////            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
////                    .body(new MeetingResponse(false, e.getMessage()));
////        }
////    }
//    @GetMapping("/participant-response")
//    public ResponseEntity<?> getParticipantDetails(
//            @RequestParam String meetingId,
//            @RequestParam String mobileNo) {
//        try {
//            return ResponseEntity.ok(employeeMeetingService.getParticipantDetails(meetingId, mobileNo));
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(new MeetingResponse(false, e.getMessage()));
//        }
//    }
//    @PostMapping("/participant-response")
//    public ResponseEntity<MeetingResponse> respondToInvitation(
//            @RequestBody ParticipantResponseDTO request) {
//
//        try {
//            String message = employeeMeetingService
//                    .respondToInvitation(request.getMeetingId(), request.getMobileNo(), request.getAction());
//
//            return ResponseEntity.ok(new MeetingResponse(true, message));
//
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(new MeetingResponse(false, e.getMessage()));
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new MeetingResponse(false, "Unable to process your response"));
//        }
//    }
//
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class MeetingResponse {
//        private boolean success;
//        private String message;
//    }
//}