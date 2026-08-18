package com.vms_backend.vms_backend.dto;

import lombok.Data;

@Data
public class EmployeeResponseDTO {

    private String employeeId;

    private String firstName;

    private String lastName;

    private String designation;

    private String sectionId;

    private String sectionName;

    private String mobileNo;

    private String emailId;

}