package com.vms_backend.vms_backend.repository;

import com.vms_backend.vms_backend.entity.VisitorHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitorHistoryRepository extends JpaRepository<VisitorHistory, Integer> {
    List<VisitorHistory> findByMobileNoOrderByChangeDateDesc(String mobileNo);
}