package com.jobportal.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.request.CreateJobRequest;
import com.jobportal.dto.request.UpdateJobRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.JobResponse;
import com.jobportal.dto.response.PageResponse;
import com.jobportal.entity.User;
import com.jobportal.enums.ExperienceLevel;
import com.jobportal.enums.JobStatus;
import com.jobportal.enums.JobType;
import com.jobportal.service.JobService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<JobResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) ExperienceLevel experience,
            @RequestParam(required = false) Double salaryMin,
            @RequestParam(required = false) Double salaryMax) {

        Page<JobResponse> result;
        if (keyword != null || location != null || categoryId != null ||
            jobType != null || experience != null || salaryMin != null || salaryMax != null) {
            result = jobService.search(keyword, location, categoryId, jobType, experience, salaryMin, salaryMax, page, size);
        } else {
            result = jobService.getAll(page, size);
        }
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getById(id)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<JobResponse>>> getMyJobs(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<JobResponse> result = jobService.getMyJobs(user, page, size);
        return ResponseEntity.ok(ApiResponse.ok(PageResponse.from(result)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> create(@Valid @RequestBody CreateJobRequest req,
                                                            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.created(jobService.create(req, user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> update(@PathVariable Long id,
                                                            @RequestBody UpdateJobRequest req,
                                                            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.update(id, req, user)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@PathVariable Long id,
                                                           @RequestBody JobStatus status,
                                                           @AuthenticationPrincipal User user) {
        jobService.updateStatus(id, status, user);
        return ResponseEntity.ok(ApiResponse.ok("Status updated", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                     @AuthenticationPrincipal User user) {
        jobService.delete(id, user);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
