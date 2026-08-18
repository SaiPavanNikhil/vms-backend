package com.vms_backend.vms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vms_backend.vms_backend.entity.EmployeeMeeting;

@Repository
public interface EmployeeMeetingRepository
        extends JpaRepository<EmployeeMeeting, Long> {

}