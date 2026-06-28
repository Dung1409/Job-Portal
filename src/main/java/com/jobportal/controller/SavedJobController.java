package com.jobportal.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.JobResponse;
import com.jobportal.dto.response.PageResponse;
import com.jobportal.entity.User;
import com.jobportal.service.SavedJobService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {
    private final SavedJobService savedJobService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<JobResponse>>> getSavedJobs(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<JobResponse> result = savedJobService.getSavedJobs(user.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    @GetMapping("/ids")
    public ResponseEntity<ApiResponse<List<Long>>> getSavedJobIds(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(savedJobService.getSavedJobIds(user.getId())));
    }

    @PostMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Void>> saveJob(@AuthenticationPrincipal User user,
                                                      @PathVariable Long jobId) {
        savedJobService.saveJob(user.getId(), jobId);
        return ResponseEntity.ok(ApiResponse.ok("Job saved", null));
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Void>> unsaveJob(@AuthenticationPrincipal User user,
                                                        @PathVariable Long jobId) {
        savedJobService.unsaveJob(user.getId(), jobId);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
