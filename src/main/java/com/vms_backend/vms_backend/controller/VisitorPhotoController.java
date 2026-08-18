package com.vms_backend.vms_backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vms_backend.vms_backend.entity.Visitors;
import com.vms_backend.vms_backend.service.VisitorService;

@RestController
@RequestMapping("/api/visitors")
public class VisitorPhotoController {

    private final VisitorService visitorService;

    public VisitorPhotoController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @GetMapping("/{mobileNo}/photo")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String mobileNo) {
        Visitors visitor = visitorService.getVisitor(mobileNo);

        if (visitor.getPhoto() == null || visitor.getPhoto().isBlank()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = Paths.get(visitor.getPhoto());
            if (!Files.exists(path)) {
                return ResponseEntity.notFound().build();
            }
            byte[] bytes = Files.readAllBytes(path);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                    .body(bytes);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}