package com.vms_backend.vms_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vms_backend.vms_backend.entity.Employee;
import com.vms_backend.vms_backend.entity.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, String> {

    boolean existsBySectionId(String sectionId);

    Optional<Section> findBySectionName(String sectionName);

    Optional<Section> findByIncharge(Employee employee);
    
    @Query(value = """
            SELECT sectionid
            FROM sections
            ORDER BY sectionid DESC
            LIMIT 1
            """, nativeQuery = true)
    String findLastSectionId();

}
