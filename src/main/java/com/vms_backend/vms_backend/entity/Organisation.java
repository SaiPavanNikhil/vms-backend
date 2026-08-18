package com.vms_backend.vms_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "organisation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organisation {

    @Id
    @Column(name = "orgcode", length = 10)
    private String orgCode;

    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @Column(name = "address1", length = 50)
    private String address1;

    @Column(name = "address2", length = 50)
    private String address2;

    @Column(name = "districtcode", length = 10)
    private String districtCode;

    @Column(name = "statecode", length = 10)
    private String stateCode;

    @Column(name = "pincode", length = 6)
    private String pincode;
}