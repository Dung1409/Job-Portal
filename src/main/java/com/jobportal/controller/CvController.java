package com.jobportal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.CvResponse;
import com.jobportal.entity.User;
import com.jobportal.service.CvService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cvs")
@RequiredArgsConstructor
public class CvController {

    private final CvService cvService;

    @PostMapping
    public ResponseEntity<ApiResponse<CvResponse>> upload(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.created(cvService.upload(user.getId(), file)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CvResponse>>> getMyCvs(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(cvService.getCvsByUser(user.getId())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        cvService.delete(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
