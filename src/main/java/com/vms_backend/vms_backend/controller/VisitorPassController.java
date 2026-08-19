package com.vms_backend.vms_backend.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vms_backend.vms_backend.dto.VisitorPassResponse;
import com.vms_backend.vms_backend.entity.Visitors;
import com.vms_backend.vms_backend.repository.VisitorRepository;
import com.vms_backend.vms_backend.service.EncryptionService;
import com.vms_backend.vms_backend.service.VisitorPassService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/visitor-pass")
@RequiredArgsConstructor
public class VisitorPassController {

    private final VisitorPassService visitorPassService;

    private final VisitorRepository visitorRepository;
    
    private final EncryptionService encryptionService;
   /*  =========================================================
     GET VISITOR PASS
    ========================================================= */

    @GetMapping("/{meetingId}")
    public ResponseEntity<?> getVisitorPass(
            @PathVariable Integer meetingId) {

        try {

            VisitorPassResponse response =
                    visitorPassService.getVisitorPass(
                            meetingId
                    );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    e.getMessage()
                            )
                    );
        }
    }
    
 // =========================================================
    // GET VISITOR PHOTO
    // =========================================================

    @GetMapping("/photo/{mobileNo}")
    public ResponseEntity<?> getVisitorPhoto(
            @PathVariable String mobileNo) {

        try {

            Visitors visitor =
                    visitorRepository.findById(mobileNo)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Visitor not found"
                                    )
                            );


            // -------------------------------------------------
            // Photo path stored in database
            // -------------------------------------------------

            String photoPath =
                    visitor.getPhoto();


            if (photoPath == null ||
                    photoPath.isBlank()) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            // -------------------------------------------------
            // Read image from backend filesystem
            // -------------------------------------------------

            Path path =
                    Paths.get(photoPath);


            if (!Files.exists(path)) {

                return ResponseEntity
                        .notFound()
                        .build();
            }


            byte[] imageBytes =
                    Files.readAllBytes(path);


            // -------------------------------------------------
            // Determine image type
            // -------------------------------------------------

            String contentType =
                    Files.probeContentType(path);


            if (contentType == null) {

                contentType =
                        MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }


            // -------------------------------------------------
            // Return image
            // -------------------------------------------------

            ByteArrayResource resource =
                    new ByteArrayResource(imageBytes);


            return ResponseEntity.ok()

                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" +
                                    path.getFileName() +
                                    "\""
                    )

                    .contentType(
                            MediaType.parseMediaType(
                                    contentType
                            )
                    )

                    .contentLength(
                            imageBytes.length
                    )

                    .body(resource);


        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Unable to load visitor photo"
                            )
                    );
        }
    }
    
    @GetMapping("/decrypt/{token}")
    public ResponseEntity<?> decryptToken(
            @PathVariable String token) {

        try {

            String decrypted =
                    encryptionService.decrypt(token);

            Integer meetingId =
                    Integer.valueOf(decrypted);

            return ResponseEntity.ok(
                    Map.of("meetingId", meetingId)
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                        Map.of(
                            "message",
                            "Invalid visitor pass token"
                        )
                    );
        }
    }
}