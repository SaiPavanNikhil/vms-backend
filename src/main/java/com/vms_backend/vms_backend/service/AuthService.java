package com.vms_backend.vms_backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vms_backend.vms_backend.dto.LoginRequest;
import com.vms_backend.vms_backend.dto.LoginResponse;
import com.vms_backend.vms_backend.entity.Employee;
import com.vms_backend.vms_backend.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;

    private final PasswordEncoder passwordEncoder;


    @Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {
	
	    // Find employee using email
	    Employee employee = employeeRepository
	            .findByEmailId(request.getEmailId())
	            .orElse(null);
	
	    // Employee not found
	    if (employee == null) {
	
	        return LoginResponse.builder()
	                .success(false)
	                .message("Invalid email or password")
	                .build();
	    }
	
	    // Check password
	    boolean passwordMatches = passwordEncoder.matches(
	            request.getPassword(),
	            employee.getPassword()
	    );
	
	    // Password doesn't match
	    if (!passwordMatches) {
	
	        return LoginResponse.builder()
	                .success(false)
	                .message("Invalid email or password")
	                .build();
	    }
	
	    // Get employee name
	    String employeeName = employee.getFirstName();
	
	    if (employee.getLastName() != null &&
	        !employee.getLastName().trim().isEmpty()) {
	
	        employeeName += " " + employee.getLastName();
	    }
	
	    // Get section ID
	    String sectionId = null;
	
	    if (employee.getSection() != null) {
	        sectionId = employee.getSection().getSectionId();
	    }
	
	    // Successful login
	    return LoginResponse.builder()
	            .success(true)
	            .message("Login successful")
	            .employeeId(employee.getEmployeeId())
	            .employeeName(employeeName)
	            .emailId(employee.getEmailId())
	            .sectionId(sectionId)
	            .build();
	}
}