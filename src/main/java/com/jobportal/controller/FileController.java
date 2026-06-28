package com.jobportal.controller;

import java.net.URLConnection;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.entity.Application;
import com.jobportal.entity.Cv;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.service.CvService;
import com.jobportal.service.FileStorageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final ApplicationRepository appRepo;
    private final FileStorageService fileStorageService;
    private final CvService cvService;

    @GetMapping("/resume/{applicationId}")
    public ResponseEntity<Resource> viewResume(@PathVariable Long applicationId) {
        Application app = appRepo.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (app.getResumeUrl() == null) {
            throw new ResourceNotFoundException("No resume for this application");
        }

        var filePath = fileStorageService.load(app.getResumeUrl());
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Resume file not found on disk");
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Could not read resume file");
        }

        String contentType = URLConnection.guessContentTypeFromName(app.getResumeUrl());
        if (contentType == null) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + app.getResumeUrl() + "\"")
                .body(resource);
    }

    @GetMapping("/cv/{cvId}")
    public ResponseEntity<Resource> viewCv(@PathVariable Long cvId) {
        Cv cv = cvService.getCvById(cvId);
        var filePath = fileStorageService.load(cv.getStoredFilename());
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("CV file not found on disk");
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Could not read CV file");
        }

        String contentType = cv.getContentType();
        if (contentType == null) contentType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + cv.getOriginalFilename() + "\"")
                .body(resource);
    }
}
