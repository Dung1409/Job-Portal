package com.jobportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.dto.request.UpdateProfileRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.entity.ApplicantProfile;
import com.jobportal.entity.User;
import com.jobportal.service.FileStorageService;
import com.jobportal.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<ApiResponse<ApplicantProfile>> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(profileService.getProfile(user.getId())));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ApplicantProfile>> updateProfile(@AuthenticationPrincipal User user,
                                                                         @RequestBody UpdateProfileRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(profileService.updateProfile(user.getId(), req)));
    }

    @PostMapping("/resume")
    public ResponseEntity<ApiResponse<ApplicantProfile>> uploadResume(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        String storedName = fileStorageService.store(file);
        return ResponseEntity.ok(ApiResponse.ok(profileService.updateResume(user.getId(), storedName)));
    }

    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<ApplicantProfile>> uploadAvatar(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        String storedName = fileStorageService.store(file);
        return ResponseEntity.ok(ApiResponse.ok(profileService.updateAvatar(user.getId(), storedName)));
    }
}
