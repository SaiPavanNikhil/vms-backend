package com.vms_backend.vms_backend.controller;

import com.vms_backend.vms_backend.dto.VisitorCheckInRequest;
import com.vms_backend.vms_backend.dto.VisitorCheckInResponse;
import com.vms_backend.vms_backend.service.VisitorCheckInService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/visitor-checkin")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class VisitorCheckInController {

    private final VisitorCheckInService visitorCheckInService;


    // =========================================================
    // API 1
    // SEARCH VISITOR BY MOBILE NUMBER
    // =========================================================

    @GetMapping("/search/{mobileNo}")
    public ResponseEntity<?> searchVisitor(
            @PathVariable String mobileNo) {

        try {

            VisitorCheckInResponse response =
                    visitorCheckInService.searchVisitor(mobileNo);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }


    // =========================================================
    // API 2
    // CHECK-IN / CHECK-OUT
    // =========================================================

    @PostMapping("/check-in-out")
    public ResponseEntity<?> checkInOut(
            @RequestBody VisitorCheckInRequest request) {

        try {

            VisitorCheckInResponse response =
                    visitorCheckInService.checkInOut(request);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }
}