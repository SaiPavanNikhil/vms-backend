package com.vms_backend.vms_backend.repository;

import com.vms_backend.vms_backend.entity.Visitors;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorRepository extends JpaRepository<Visitors, String> {
}