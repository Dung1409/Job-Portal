package com.jobportal.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.dto.request.ApplyJobRequest;
import com.jobportal.dto.request.UpdateApplicationStatusRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.ApplicationResponse;
import com.jobportal.dto.response.PageResponse;
import com.jobportal.entity.User;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.CvService;
import com.jobportal.service.FileStorageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService appService;
    private final FileStorageService fileStorageService;
    private final CvService cvService;

    @GetMapping("/check/{jobId}")
    public ResponseEntity<ApiResponse<Boolean>> hasApplied(@AuthenticationPrincipal User user,
                                                           @PathVariable Long jobId) {
        return ResponseEntity.ok(ApiResponse.ok(appService.hasApplied(user.getId(), jobId)));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(
            @AuthenticationPrincipal User user,
            @RequestParam("jobId") @jakarta.validation.constraints.NotNull Long jobId,
            @RequestParam(value = "coverLetter", required = false) String coverLetter,
            @RequestPart(value = "resume", required = false) MultipartFile resume,
            @RequestParam(value = "cvId", required = false) Long cvId) {
        ApplyJobRequest req = ApplyJobRequest.builder().jobId(jobId).coverLetter(coverLetter).build();
        String resumeUrl = null;
        if (resume != null && !resume.isEmpty()) {
            resumeUrl = fileStorageService.store(resume);
        } else if (cvId != null) {
            var cv = cvService.getCvById(cvId);
            if (!cv.getUserId().equals(user.getId())) {
                throw new com.jobportal.exception.UnauthorizedException("CV does not belong to you");
            }
            resumeUrl = cv.getStoredFilename();
        }
        return ResponseEntity.ok(ApiResponse.created(appService.apply(user.getId(), req, resumeUrl)));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ApplicationResponse>> applyJson(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ApplyJobRequest req) {
        return ResponseEntity.ok(ApiResponse.created(appService.apply(user.getId(), req, null)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<ApplicationResponse>>> getMyApplications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ApplicationResponse> result = appService.getMyApplications(user.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> withdraw(@PathVariable Long id,
                                                       @AuthenticationPrincipal User user) {
        appService.withdraw(id, user.getId());
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<ApiResponse<?>> getByJob(@PathVariable Long jobId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Page<ApplicationResponse> result = appService.getByJob(jobId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(appService.getById(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateApplicationStatusRequest req,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(appService.updateStatus(id, req, user)));
    }
}
