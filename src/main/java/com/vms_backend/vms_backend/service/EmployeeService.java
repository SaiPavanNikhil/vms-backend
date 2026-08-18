package com.vms_backend.vms_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vms_backend.vms_backend.dto.EmployeeRequestDTO;
import com.vms_backend.vms_backend.dto.EmployeeResponseDTO;
import com.vms_backend.vms_backend.entity.Employee;
import com.vms_backend.vms_backend.entity.Section;
import com.vms_backend.vms_backend.repository.EmployeeRepository;
import com.vms_backend.vms_backend.repository.SectionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final SectionRepository sectionRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get All Employees
     */
    public List<EmployeeResponseDTO> getAllEmployees() {

        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeResponseDTO> response = new ArrayList<>();

        for (Employee employee : employees) {

            EmployeeResponseDTO dto = new EmployeeResponseDTO();

            dto.setEmployeeId(employee.getEmployeeId());
            dto.setFirstName(employee.getFirstName());
            dto.setLastName(employee.getLastName());
            dto.setDesignation(employee.getDesignation());
            dto.setMobileNo(employee.getMobileNo());
            dto.setEmailId(employee.getEmailId());

            if (employee.getSection() != null) {

                dto.setSectionId(employee.getSection().getSectionId());
                dto.setSectionName(employee.getSection().getSectionName());

            }

            response.add(dto);

        }

        return response;
    }

    /**
     * Get Employee By Id
     */
    public EmployeeResponseDTO getEmployeeById(String employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeResponseDTO dto = new EmployeeResponseDTO();

        dto.setEmployeeId(employee.getEmployeeId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setDesignation(employee.getDesignation());
        dto.setMobileNo(employee.getMobileNo());
        dto.setEmailId(employee.getEmailId());

        if (employee.getSection() != null) {

            dto.setSectionId(employee.getSection().getSectionId());
            dto.setSectionName(employee.getSection().getSectionName());

        }

        return dto;
    }

    /**
     * Add Employee
     */
    public String addEmployee(EmployeeRequestDTO request) {

        Employee employee = new Employee();

        employee.setEmployeeId(generateEmployeeId());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setDesignation(request.getDesignation());
        employee.setMobileNo(request.getMobileNo());
        employee.setEmailId(request.getEmailId());
        
        employee.setPassword(
                passwordEncoder.encode(request.getPassword())
            );

        if (request.getSectionId() != null &&
                !request.getSectionId().isBlank()) {

            Section section = sectionRepository.findById(request.getSectionId())
                    .orElseThrow(() -> new RuntimeException("Section not found"));

            employee.setSection(section);

        }

        employeeRepository.save(employee);

        return "Employee Added Successfully";
    }

    /**
     * Update Employee
     */
    public String updateEmployee(String employeeId,
                                 EmployeeRequestDTO request) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setDesignation(request.getDesignation());
        employee.setMobileNo(request.getMobileNo());
        employee.setEmailId(request.getEmailId());
        
        if (request.getPassword() != null &&
        	    !request.getPassword().trim().isEmpty()) {

        	    employee.setPassword(
        	        passwordEncoder.encode(request.getPassword())
        	    );

        	}

        if (request.getSectionId() != null &&
                !request.getSectionId().isBlank()) {

            Section section = sectionRepository.findById(request.getSectionId())
                    .orElseThrow(() -> new RuntimeException("Section not found"));

            employee.setSection(section);

        } else {

            employee.setSection(null);

        }

        employeeRepository.save(employee);

        return "Employee Updated Successfully";
    }

    /**
     * Delete Employee
     */
    public String deleteEmployee(String employeeId) {

        if (!employeeRepository.existsById(employeeId)) {

            throw new RuntimeException("Employee not found");

        }

        employeeRepository.deleteById(employeeId);

        return "Employee Deleted Successfully";
    }

    /**
     * Generate Employee Id
     */
    private String generateEmployeeId() {

        String lastId = employeeRepository.findLastEmployeeId();

        if (lastId == null) {

            return "EMP001";

        }

        int number = Integer.parseInt(lastId.substring(3));

        return String.format("EMP%03d", number + 1);

    }

}