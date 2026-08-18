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
@Table(name = "sections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Section {

    @Id
    @Column(name = "sectionid", length = 10)
    private String sectionId;

    @Column(name = "sectionname", nullable = false, length = 15)
    private String sectionName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inchargeid")
    private Employee incharge;
}