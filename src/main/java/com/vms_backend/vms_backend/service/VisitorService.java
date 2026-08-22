package com.vms_backend.vms_backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vms_backend.vms_backend.dto.VisitorCheckInRequest;
import com.vms_backend.vms_backend.dto.VisitorCheckInResponse;
import com.vms_backend.vms_backend.dto.VisitorRequest;
import com.vms_backend.vms_backend.entity.VisitorHistory;
import com.vms_backend.vms_backend.entity.Visitors;
import com.vms_backend.vms_backend.repository.VisitorHistoryRepository;
import com.vms_backend.vms_backend.repository.VisitorRepository;

@Service
public class VisitorService {

    private static final String UPLOAD_DIR = "D:\\uploadings";

    private final VisitorRepository visitorRepo;
    private final VisitorHistoryRepository historyRepo;
    private final VisitorCheckInService visitorCheckInService;

    public VisitorService(
            VisitorRepository visitorRepo,
            VisitorHistoryRepository historyRepo,
            VisitorCheckInService visitorCheckInService) {

        this.visitorRepo = visitorRepo;
        this.historyRepo = historyRepo;
        this.visitorCheckInService = visitorCheckInService;
    }
    
    private String savePhoto(String mobileNo, String dataUrl) {
        if (dataUrl == null || dataUrl.isBlank()) return null;

        try {
            Path dir = Paths.get(UPLOAD_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            String base64 = dataUrl.contains(",") ? dataUrl.substring(dataUrl.indexOf(",") + 1) : dataUrl;
            byte[] bytes = Base64.getDecoder().decode(base64);

            String fileName = mobileNo + "_" + System.currentTimeMillis() + ".jpg";
            Path filePath = dir.resolve(fileName);
            Files.write(filePath, bytes);

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save visitor photo: " + e.getMessage(), e);
        }
    }
    @Transactional
    public Visitors createVisitor(VisitorRequest req) {

        if (visitorRepo.existsById(req.getMobileNo())) {
            throw new IllegalStateException("Visitor already registered with this mobile number");
        }

        Visitors v = new Visitors();

        v.setMobileNo(req.getMobileNo());
        v.setFirstName(req.getFirstName());
        v.setLastName(req.getLastName());
        v.setAddress(req.getAddress());
        v.setState(req.getState());
        v.setDistrict(req.getDistrict());
        v.setOrganisation(req.getOrganisation());
        v.setEmail(req.getEmail());
        v.setPhoto(savePhoto(req.getMobileNo(), req.getPhotoDataUrl()));
        v.setModeOfVisit(req.getModeOfVisit());
        v.setRegistrationDate(LocalDate.now());
        v.setPurposeOfVisit(req.getPurposeOfVisit());
        v.setDescriptionOfVisit(req.getDescriptionOfVisit());
         
        System.out.println("========== CREATE VISITOR ==========");
        System.out.println("Mobile No: " + req.getMobileNo());
        System.out.println("First Name: " + req.getFirstName());
        System.out.println("Last Name: " + req.getLastName());
        System.out.println("Address: " + req.getAddress());
        System.out.println("Email: " + req.getEmail());
        System.out.println("Photo Path: " + v.getPhoto());
        System.out.println("Mode of Visit: " + req.getModeOfVisit());
        System.out.println("Purpose: " + req.getPurposeOfVisit());
        System.out.println("====================================");

        return visitorRepo.save(v);
    }
//    @Transactional
//    public Visitors createVisitor(VisitorRequest req) {
//        if (visitorRepo.existsById(req.getMobileNo())) {
//            throw new IllegalStateException("Visitor already registered with this mobile number");
//        }
//        Visitors v = new Visitors();
//        v.setMobileNo(req.getMobileNo());
//        v.setFirstName(req.getFirstName());
//        v.setLastName(req.getLastName());
//        v.setAddress(req.getAddress());
//        v.setState(req.getState());
//        v.setDistrict(req.getDistrict());
//        v.setOrganisation(req.getOrganisation());
//        v.setEmail(req.getEmail());
//        v.setPhoto(savePhoto(req.getMobileNo(), req.getPhotoDataUrl()));
//        v.setModeOfVisit(req.getModeOfVisit());
//        v.setRegistrationDate(LocalDate.now());
//        v.setPurposeOfVisit(req.getPurposeOfVisit());
//        return visitorRepo.save(v);
//    }

    @Transactional
    public Visitors updateVisitor(String mobileNo, VisitorRequest req) {
        Visitors existing = visitorRepo.findById(mobileNo)
                .orElseThrow(() -> new NoSuchElementException("Visitor not found: " + mobileNo));

        VisitorHistory h = new VisitorHistory();
        h.setMobileNo(existing.getMobileNo());
        h.setFirstName(existing.getFirstName());
        h.setLastName(existing.getLastName());
        h.setAddress(existing.getAddress());
        h.setState(existing.getState());
        h.setDistrict(existing.getDistrict());
        h.setOrganisation(existing.getOrganisation());
        h.setEmail(existing.getEmail());
        h.setPhoto(existing.getPhoto());
        h.setRegistrationDate(existing.getRegistrationDate());
        h.setRegistrationDate(existing.getRegistrationDate());
        h.setChangeDate(LocalDate.now());
        historyRepo.save(h);

        existing.setFirstName(req.getFirstName());
        existing.setLastName(req.getLastName());
        existing.setAddress(req.getAddress());
        existing.setState(req.getState());
        existing.setDistrict(req.getDistrict());
        existing.setOrganisation(req.getOrganisation());
        existing.setEmail(req.getEmail());
        existing.setModeOfVisit(req.getModeOfVisit());
        existing.setPurposeOfVisit(req.getPurposeOfVisit());
        existing.setDescriptionOfVisit(req.getDescriptionOfVisit());        
        
        if (req.getPhotoDataUrl() != null && !req.getPhotoDataUrl().isBlank()) {
            existing.setPhoto(savePhoto(mobileNo, req.getPhotoDataUrl()));
        }
        return visitorRepo.save(existing);
    }

    public Visitors getVisitor(String mobileNo) {
        return visitorRepo.findById(mobileNo)
                .orElseThrow(() -> new NoSuchElementException("Visitor not found: " + mobileNo));
    }
    

	@Transactional
	public VisitorCheckInResponse checkInOut(
	        VisitorCheckInRequest request) {
	
	    return visitorCheckInService.checkInOut(request);
	}

    public List<Visitors> getAllVisitors() {
        return visitorRepo.findAll();
    }

    public List<VisitorHistory> getHistory(String mobileNo) {
        return historyRepo.findByMobileNoOrderByChangeDateDesc(mobileNo);
    }
}