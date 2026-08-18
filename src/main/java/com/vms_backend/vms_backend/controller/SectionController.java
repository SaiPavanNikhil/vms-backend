package com.vms_backend.vms_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vms_backend.vms_backend.dto.SectionRequestDTO;
import com.vms_backend.vms_backend.dto.SectionResponseDTO;
import com.vms_backend.vms_backend.service.SectionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    /**
     * Get All Sections
     */
    @GetMapping
    public ResponseEntity<List<SectionResponseDTO>> getAllSections() {
        return ResponseEntity.ok(sectionService.getAllSections());
    }

    /**
     * Get Section By Id
     */
    @GetMapping("/{sectionId}")
    public ResponseEntity<SectionResponseDTO> getSectionById(
            @PathVariable String sectionId) {

        return ResponseEntity.ok(sectionService.getSectionById(sectionId));
    }

    /**
     * Add Section
     */
    @PostMapping
    public ResponseEntity<String> addSection(
            @RequestBody SectionRequestDTO request) {

        return ResponseEntity.ok(sectionService.addSection(request));
    }

    /**
     * Update Section
     */
    @PutMapping("/{sectionId}")
    public ResponseEntity<String> updateSection(
            @PathVariable String sectionId,
            @RequestBody SectionRequestDTO request) {

        return ResponseEntity.ok(
                sectionService.updateSection(sectionId, request)
        );
    }

    /**
     * Delete Section
     */
    @DeleteMapping("/{sectionId}")
    public ResponseEntity<String> deleteSection(
            @PathVariable String sectionId) {

        return ResponseEntity.ok(
                sectionService.deleteSection(sectionId)
        );
    }

}