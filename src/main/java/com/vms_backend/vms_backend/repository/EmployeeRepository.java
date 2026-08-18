package com.vms_backend.vms_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vms_backend.vms_backend.entity.Employee;
import com.vms_backend.vms_backend.entity.Section;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    List<Employee> findBySection(Section section);

    boolean existsByEmployeeId(String employeeId);
    
    @Query(value = """
            SELECT employeeid
            FROM employees
            ORDER BY employeeid DESC
            LIMIT 1
            """, nativeQuery = true)
    String findLastEmployeeId();
    
    
    	Optional<Employee> findByEmailId(String email);

}
