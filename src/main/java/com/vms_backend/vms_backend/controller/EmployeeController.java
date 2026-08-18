package com.vms_backend.vms_backend.controller;

import com.vms_backend.vms_backend.dto.EmployeeRequestDTO;
import com.vms_backend.vms_backend.dto.EmployeeResponseDTO;
import com.vms_backend.vms_backend.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Get All Employees
     */
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {

        return ResponseEntity.ok(employeeService.getAllEmployees());

    }

    /**
     * Get Employee By Id
     */
    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(
            @PathVariable String employeeId) {

        return ResponseEntity.ok(
                employeeService.getEmployeeById(employeeId)
        );

    }

    /**
     * Add Employee
     */
    @PostMapping
    public ResponseEntity<String> addEmployee(
            @RequestBody EmployeeRequestDTO request) {

        return ResponseEntity.ok(
                employeeService.addEmployee(request)
        );

    }

    /**
     * Update Employee
     */
    @PutMapping("/{employeeId}")
    public ResponseEntity<String> updateEmployee(
            @PathVariable String employeeId,
            @RequestBody EmployeeRequestDTO request) {

        return ResponseEntity.ok(
                employeeService.updateEmployee(employeeId, request)
        );

    }

    /**
     * Delete Employee
     */
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable String employeeId) {

        return ResponseEntity.ok(
                employeeService.deleteEmployee(employeeId)
        );

    }

}