package com.vms_backend.vms_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vms_backend.vms_backend.dto.VisitorMeetingApprovalRequest;
import com.vms_backend.vms_backend.dto.VisitorMeetingRequest;
import com.vms_backend.vms_backend.dto.VisitorMeetingResponse;
import com.vms_backend.vms_backend.entity.VisitorMeeting;
import com.vms_backend.vms_backend.service.EncryptionService;
import com.vms_backend.vms_backend.service.VisitorMeetingService;
import com.vms_backend.vms_backend.util.TokenUtil;

@RestController
@RequestMapping("/api/meetings")
public class VisitorMeetingController {

    private final VisitorMeetingService meetingService;
    
    @Autowired
    private EncryptionService encryptionService;

    public VisitorMeetingController(VisitorMeetingService meetingService) {
        this.meetingService = meetingService;
    }

    // Called by the visitor form after a visitor is saved
    @PostMapping
    public VisitorMeeting create(@RequestBody VisitorMeetingRequest req) throws Exception {
        return meetingService.createRequest(req);
    }

    // Admin/host-approval page: all requests across all hosts
    @GetMapping
    public List<VisitorMeetingResponse> getAll() {
        return meetingService.getAllRequests();
    }

    // If a specific host logs in and should only see their own requests
    @GetMapping("/host/{hostId}")
    public List<VisitorMeetingResponse> getForHost(@PathVariable String hostId) {
        return meetingService.getRequestsForHost(hostId);
    }

    @PutMapping("/{meetingId}/approve")
    public VisitorMeeting approve(@PathVariable Integer meetingId,
                                   @RequestBody(required = false) VisitorMeetingApprovalRequest req) {
        return meetingService.approve(meetingId, req != null ? req : new VisitorMeetingApprovalRequest());
    }

    @PutMapping("/{meetingId}/reject")
    public VisitorMeeting reject(@PathVariable Integer meetingId) {
        return meetingService.reject(meetingId);
    }

    @PutMapping("/{meetingId}/hold")
    public VisitorMeeting hold(@PathVariable Integer meetingId,
                                @RequestBody(required = false) VisitorMeetingApprovalRequest req) {
        return meetingService.hold(meetingId, req != null ? req : new VisitorMeetingApprovalRequest());
    }

    @GetMapping("/latest")
    public ResponseEntity<VisitorMeetingResponse> getLatest(
            @RequestParam String hostId,
            @RequestParam String mobileNo) {
        return meetingService.getLatestRequest(hostId, mobileNo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Called for a bare ?hostId=EMP001 link — finds that host's single most
    // recent request (any visitor) so the frontend can redirect into the
    // encrypted hostId+mobileNo token for that record.
    @GetMapping("/host/{hostId}/latest")
    public ResponseEntity<VisitorMeetingResponse> getLatestForHost(@PathVariable String hostId) {
        return meetingService.getLatestRequestForHost(hostId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // The HostApproval page hits this first with the raw token from the email link,
    // gets back the meetingId + visitor/host details, then calls approve/reject/hold
    // above with that meetingId.
//    @GetMapping("/resolve")
//    public ResponseEntity<VisitorMeetingResponse> resolve(@RequestParam String token) {
//        try {
//            String[] parts = TokenUtil.decode(token); // [hostId, mobileNo]
//            return meetingService.getLatestRequest(parts[0], parts[1])
//                    .map(ResponseEntity::ok)
//                    .orElseGet(() -> ResponseEntity.notFound().build());
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
    
    @GetMapping("/resolve")
    public ResponseEntity<VisitorMeetingResponse> resolve(
            @RequestParam String token) {

        try {

            // Decrypt token
            String payload = encryptionService.decrypt(token);

            // Example:
            // payload = hostId=123&mobileNo=9876543210

            String[] params = payload.split("&");

            String hostId = params[0].split("=", 2)[1];
            String mobileNo = params[1].split("=", 2)[1];

            return meetingService
                    .getLatestRequest(hostId, mobileNo)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.badRequest().build();
        }
    }
}
//package com.vms_backend.vms_backend.controller;
//
//import java.util.List;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import com.vms_backend.vms_backend.dto.VisitorMeetingApprovalRequest;
//import com.vms_backend.vms_backend.dto.VisitorMeetingRequest;
//import com.vms_backend.vms_backend.dto.VisitorMeetingResponse;
//import com.vms_backend.vms_backend.entity.VisitorMeeting;
//import com.vms_backend.vms_backend.service.VisitorMeetingService;
//
//@RestController
//@RequestMapping("/api/meetings")
//public class VisitorMeetingController {
//
//    private final VisitorMeetingService meetingService;
//
//    public VisitorMeetingController(VisitorMeetingService meetingService) {
//        this.meetingService = meetingService;
//    }
//
//    // Called by the visitor form after a visitor is saved
//    @PostMapping
//    public VisitorMeeting create(@RequestBody VisitorMeetingRequest req) {
//        return meetingService.createRequest(req);
//    }
//
//    // Admin/host-approval page: all requests across all hosts
//    @GetMapping
//    public List<VisitorMeetingResponse> getAll() {
//        return meetingService.getAllRequests();
//    }
//
//    // If a specific host logs in and should only see their own requests
//    @GetMapping("/host/{hostId}")
//    public List<VisitorMeetingResponse> getForHost(@PathVariable String hostId) {
//        return meetingService.getRequestsForHost(hostId);
//    }
//
//    @PutMapping("/{meetingId}/approve")
//    public VisitorMeeting approve(@PathVariable Integer meetingId,
//                                   @RequestBody(required = false) VisitorMeetingApprovalRequest req) {
//        return meetingService.approve(meetingId, req != null ? req : new VisitorMeetingApprovalRequest());
//    }
//
//    @PutMapping("/{meetingId}/reject")
//    public VisitorMeeting reject(@PathVariable Integer meetingId) {
//        return meetingService.reject(meetingId);
//    }
//    @PutMapping("/{meetingId}/hold")
//    public VisitorMeeting hold(@PathVariable Integer meetingId,
//                                @RequestBody(required = false) VisitorMeetingApprovalRequest req) {
//        return meetingService.hold(meetingId, req != null ? req : new VisitorMeetingApprovalRequest());
//    }
//    @GetMapping("/latest")
//    public ResponseEntity<VisitorMeetingResponse> getLatest(
//            @RequestParam String hostId,
//            @RequestParam String mobileNo) {
//        return meetingService.getLatestRequest(hostId, mobileNo)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
// 
//    // Called for a bare ?hostId=EMP001 link — finds that host's single most
//    // recent request (any visitor) so the frontend can redirect into the
//    // encrypted hostId+mobileNo token for that record.
//    @GetMapping("/host/{hostId}/latest")
//    public ResponseEntity<VisitorMeetingResponse> getLatestForHost(@PathVariable String hostId) {
//        return meetingService.getLatestRequestForHost(hostId)
//                .map(ResponseEntity::ok)
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//}