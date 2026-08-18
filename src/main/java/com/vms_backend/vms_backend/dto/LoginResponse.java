package com.vms_backend.vms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private boolean success;

    private String message;

    private String employeeId;

    private String employeeName;

    private String emailId;

    private String sectionId;
}
