package com.jobportal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.dto.request.ApplyJobRequest;
import com.jobportal.dto.request.UpdateApplicationStatusRequest;
import com.jobportal.dto.response.ApplicationResponse;
import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.enums.ApplicationStatus;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.JobRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository appRepo;
    private final JobRepository jobRepo;

    public boolean hasApplied(Long userId, Long jobId) {
        return appRepo.existsByJobIdAndApplicantId(jobId, userId);
    }

    public Page<ApplicationResponse> getMyApplications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return appRepo.findByApplicantId(userId, pageable).map(this::toResponse);
    }

    public List<ApplicationResponse> getByJob(Long jobId) {
        return appRepo.findByJobId(jobId).stream().map(this::toResponse).toList();
    }

    public Page<ApplicationResponse> getByJob(Long jobId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return appRepo.findByJobId(jobId, pageable).map(this::toResponse);
    }

    @Transactional
    public ApplicationResponse apply(Long userId, ApplyJobRequest req, String resumeUrl) {
        if (appRepo.existsByJobIdAndApplicantId(req.getJobId(), userId)) {
            throw new DuplicateResourceException("Already applied to this job");
        }

        Job job = jobRepo.findById(req.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        User applicant = new User();
        applicant.setId(userId);

        Application app = Application.builder()
                .job(job)
                .applicant(applicant)
                .coverLetter(req.getCoverLetter())
                .resumeUrl(resumeUrl)
                .status(ApplicationStatus.APPLIED)
                .appliedAt(LocalDateTime.now())
                .build();

        return toResponse(appRepo.save(app));
    }

    @Transactional
    public void withdraw(Long id, Long userId) {
        Application app = appRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!app.getApplicant().getId().equals(userId)) {
            throw new UnauthorizedException("You can only withdraw your own applications");
        }

        if (app.getStatus() != ApplicationStatus.APPLIED) {
            throw new BadRequestException("Can only withdraw applications with status APPLIED");
        }

        appRepo.delete(app);
    }

    @Transactional
    public ApplicationResponse updateStatus(Long id, UpdateApplicationStatusRequest req, User recruiter) {
        Application app = appRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!app.getJob().getCompany().getRecruiter().getId().equals(recruiter.getId())) {
            throw new UnauthorizedException("You are not authorized to update this application");
        }

        app.setStatus(req.getStatus());
        app.setNote(req.getNote());
        app.setUpdatedAt(LocalDateTime.now());

        return toResponse(appRepo.save(app));
    }

    public ApplicationResponse getById(Long id) {
        Application app = appRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        return toResponse(app);
    }

    private ApplicationResponse toResponse(Application app) {
        return ApplicationResponse.builder()
                .id(app.getId())
                .jobId(app.getJob().getId())
                .jobTitle(app.getJob().getTitle())
                .applicantId(app.getApplicant().getId())
                .applicantName(app.getApplicant().getFullName())
                .applicantEmail(app.getApplicant().getEmail())
                .coverLetter(app.getCoverLetter())
                .resumeUrl(app.getResumeUrl())
                .status(app.getStatus())
                .note(app.getNote())
                .appliedAt(app.getAppliedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }
}
