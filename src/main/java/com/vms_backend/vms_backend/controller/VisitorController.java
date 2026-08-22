package com.vms_backend.vms_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vms_backend.vms_backend.dto.VisitorCheckInRequest;
import com.vms_backend.vms_backend.dto.VisitorCheckInResponse;
import com.vms_backend.vms_backend.dto.VisitorRequest;
import com.vms_backend.vms_backend.entity.VisitorHistory;
import com.vms_backend.vms_backend.entity.Visitors;
import com.vms_backend.vms_backend.service.VisitorService;

@RestController
@RequestMapping("/api/visitors")
public class VisitorController {

    private final VisitorService service;

    public VisitorController(VisitorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Visitors> create(@RequestBody VisitorRequest req) {
        return ResponseEntity.ok(service.createVisitor(req));
    }

    @PutMapping("/{mobileNo}")
    public ResponseEntity<Visitors> update(@PathVariable String mobileNo, @RequestBody VisitorRequest req) {
        return ResponseEntity.ok(service.updateVisitor(mobileNo, req));
    }

    @GetMapping("/{mobileNo}")
    public ResponseEntity<Visitors> get(@PathVariable String mobileNo) {
        return ResponseEntity.ok(service.getVisitor(mobileNo));
    }

    @GetMapping
    public ResponseEntity<List<Visitors>> getAll() {
        return ResponseEntity.ok(service.getAllVisitors());
    }

    @GetMapping("/{mobileNo}/history")
    public ResponseEntity<List<VisitorHistory>> history(@PathVariable String mobileNo) {
        return ResponseEntity.ok(service.getHistory(mobileNo));
    }
    
    @PostMapping("/check-in-out")
    public ResponseEntity<VisitorCheckInResponse> checkInOut(
            @RequestBody VisitorCheckInRequest request) {

        return ResponseEntity.ok(
                service.checkInOut(request)
        );
    }
}