package com.vms_backend.vms_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @Column(name = "employeeid", length = 10)
    private String employeeId;

    @Column(name = "firstname", nullable = false, length = 15)
    private String firstName;

    @Column(name = "lastname", length = 20)
    private String lastName;

    @Column(name = "designation", length = 20)
    private String designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sectionid")
    private Section section;

    @Column(name = "mobileno", length = 10)
    private String mobileNo;

    @Column(name = "emailid", length = 50)
    private String emailId;
    
    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "orgcode", length = 10)
    private String orgcode;

}
