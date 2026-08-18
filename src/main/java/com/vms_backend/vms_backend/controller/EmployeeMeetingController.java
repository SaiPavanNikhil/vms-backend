package com.vms_backend.vms_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vms_backend.vms_backend.dto.EmployeeMeetingRequestDTO;
import com.vms_backend.vms_backend.dto.ParticipantResponseDTO;
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
                    .respondToInvitation(request.getToken(), request.getAction());

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
    @GetMapping("/participant-response/{token}")
    public ResponseEntity<?> getParticipantDetails(@PathVariable String token) {
        try {
            return ResponseEntity.ok(employeeMeetingService.getParticipantDetails(token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MeetingResponse(false, e.getMessage()));
        }
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MeetingResponse {
        private boolean success;
        private String message;
    }
}
//package com.vms_backend.vms_backend.controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.vms_backend.vms_backend.dto.EmployeeMeetingRequestDTO;
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
////@CrossOrigin(origins = "http://localhost:4200")
//public class EmployeeMeetingController {
//
//    private final EmployeeMeetingService employeeMeetingService;
//
//
//    @PostMapping
//    public ResponseEntity<MeetingResponse> scheduleMeeting(
//            @RequestBody EmployeeMeetingRequestDTO request) {
//
//        try {
//
//            String message =
//                    employeeMeetingService.scheduleMeeting(request);
//
//            return ResponseEntity.ok(
//                    new MeetingResponse(
//                            true,
//                            message
//                    )
//            );
//
//        } catch (RuntimeException e) {
//
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body(
//                            new MeetingResponse(
//                                    false,
//                                    e.getMessage()
//                            )
//                    );
//
//        } catch (Exception e) {
//
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(
//                            new MeetingResponse(
//                                    false,
//                                    "Unable to schedule meeting"
//                            )
//                    );
//        }
//    }
//
//
//    // ==========================================
//    // RESPONSE DTO
//    // ==========================================
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class MeetingResponse {
//
//        private boolean success;
//
//        private String message;
//    }
//}