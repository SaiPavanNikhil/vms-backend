package com.vms_backend.vms_backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vms_backend.vms_backend.dto.SectionRequestDTO;
import com.vms_backend.vms_backend.dto.SectionResponseDTO;
import com.vms_backend.vms_backend.entity.Employee;
import com.vms_backend.vms_backend.entity.Section;
import com.vms_backend.vms_backend.repository.EmployeeRepository;
import com.vms_backend.vms_backend.repository.SectionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * Get all sections
     */
    public List<SectionResponseDTO> getAllSections() {

        List<Section> sections = sectionRepository.findAll();
        List<SectionResponseDTO> response = new ArrayList<>();

        for (Section section : sections) {

            SectionResponseDTO dto = new SectionResponseDTO();

            dto.setSectionId(section.getSectionId());
            dto.setSectionName(section.getSectionName());

            if (section.getIncharge() != null) {
                dto.setInchargeId(section.getIncharge().getEmployeeId());
                dto.setInchargeName(
                        section.getIncharge().getFirstName() + " " +
                        section.getIncharge().getLastName()
                );
            }

            response.add(dto);
        }

        return response;
    }

    /**
     * Get Section By Id
     */
    public SectionResponseDTO getSectionById(String sectionId) {

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        SectionResponseDTO dto = new SectionResponseDTO();

        dto.setSectionId(section.getSectionId());
        dto.setSectionName(section.getSectionName());

        if (section.getIncharge() != null) {
            dto.setInchargeId(section.getIncharge().getEmployeeId());
            dto.setInchargeName(
                    section.getIncharge().getFirstName() + " " +
                    section.getIncharge().getLastName()
            );
        }

        return dto;
    }

    /**
     * Add Section
     */
    public String addSection(SectionRequestDTO request) {

        Section section = new Section();

        section.setSectionId(generateSectionId());
        section.setSectionName(request.getSectionName());

        if (request.getInchargeId() != null &&
                !request.getInchargeId().isBlank()) {

            Employee employee = employeeRepository.findById(request.getInchargeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            section.setIncharge(employee);
        }

        sectionRepository.save(section);

        return "Section Added Successfully";
    }

    /**
     * Update Section
     */
    public String updateSection(String sectionId,
                                SectionRequestDTO request) {

        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Section not found"));

        section.setSectionName(request.getSectionName());

        if (request.getInchargeId() != null &&
                !request.getInchargeId().isBlank()) {

            Employee employee = employeeRepository.findById(request.getInchargeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            section.setIncharge(employee);
        } else {
            section.setIncharge(null);
        }

        sectionRepository.save(section);

        return "Section Updated Successfully";
    }

    /**
     * Delete Section
     */
    public String deleteSection(String sectionId) {

        if (!sectionRepository.existsById(sectionId)) {
            throw new RuntimeException("Section not found");
        }

        sectionRepository.deleteById(sectionId);

        return "Section Deleted Successfully";
    }

    /**
     * Generate Section Id
     */
    private String generateSectionId() {

        String lastId = sectionRepository.findLastSectionId();

        if (lastId == null) {
            return "SEC001";
        }

        int number = Integer.parseInt(lastId.substring(3));

        return String.format("SEC%03d", number + 1);
    }

}
