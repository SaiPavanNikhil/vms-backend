package com.vms_backend.vms_backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeMeetingRequestDTO {

    private String employeeId;

    private String meetingTitle;

    private String meetingPurpose;

    private LocalDate meetingDate;

    private LocalTime meetingTime;

    private List<ParticipantDTO> participants;
    private String venue;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipantDTO {

        private String name;

        private String email;

        private String mobileNo;

        private String organisation;
    }
}