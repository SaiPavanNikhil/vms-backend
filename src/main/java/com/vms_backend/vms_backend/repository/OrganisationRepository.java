package com.vms_backend.vms_backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vms_backend.vms_backend.entity.Organisation;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, String> {

}