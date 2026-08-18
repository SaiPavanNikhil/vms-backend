package com.vms_backend.vms_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponseDTO {
    private String token;
    private String action; // "approve" or "reject"
}