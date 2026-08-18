package com.vms_backend.vms_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vms_backend.vms_backend.entity.EmployeeMeetingParticipant;

@Repository
public interface EmployeeMeetingParticipantRepository
        extends JpaRepository<EmployeeMeetingParticipant, Long> {

    Optional<EmployeeMeetingParticipant> findByApprovalToken(String approvalToken);
}
//package com.vms_backend.vms_backend.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import com.vms_backend.vms_backend.entity.EmployeeMeetingParticipant;
//
//@Repository
//public interface EmployeeMeetingParticipantRepository
//        extends JpaRepository<EmployeeMeetingParticipant, Long> {
//
//}