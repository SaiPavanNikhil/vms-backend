package com.vms_backend.vms_backend.dto;

import lombok.Data;

@Data
public class EmployeeRequestDTO {

    private String firstName;

    private String lastName;

    private String designation;

    private String sectionId;

    private String mobileNo;

    private String emailId;
    
    private String password;

}