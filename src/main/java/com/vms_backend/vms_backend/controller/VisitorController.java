package com.vms_backend.vms_backend.controller;

import com.vms_backend.vms_backend.dto.VisitorRequest;
import com.vms_backend.vms_backend.entity.Visitors;
import com.vms_backend.vms_backend.entity.VisitorHistory;
import com.vms_backend.vms_backend.service.VisitorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}