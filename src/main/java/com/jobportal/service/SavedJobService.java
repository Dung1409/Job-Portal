package com.jobportal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.dto.response.JobResponse;
import com.jobportal.entity.Job;
import com.jobportal.entity.SavedJob;
import com.jobportal.entity.User;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.SavedJobRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository savedJobRepo;
    private final JobRepository jobRepo;

    public Page<JobResponse> getSavedJobs(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return savedJobRepo.findByUserId(userId, pageable)
                .map(sj -> toJobResponse(sj.getJob()));
    }

    public List<Long> getSavedJobIds(Long userId) {
        return savedJobRepo.findByUserId(userId).stream()
                .map(sj -> sj.getJob().getId())
                .toList();
    }

    @Transactional
    public void saveJob(Long userId, Long jobId) {
        if (savedJobRepo.existsByUserIdAndJobId(userId, jobId)) {
            throw new DuplicateResourceException("Job already saved");
        }

        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        User user = new User();
        user.setId(userId);

        savedJobRepo.save(SavedJob.builder()
                .user(user)
                .job(job)
                .savedAt(LocalDateTime.now())
                .build());
    }

    @Transactional
    public void unsaveJob(Long userId, Long jobId) {
        if (!savedJobRepo.existsByUserIdAndJobId(userId, jobId)) {
            throw new ResourceNotFoundException("Saved job not found");
        }
        savedJobRepo.deleteByUserIdAndJobId(userId, jobId);
    }

    private JobResponse toJobResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .currency(job.getCurrency())
                .jobType(job.getJobType())
                .experience(job.getExperience())
                .status(job.getStatus())
                .companyId(job.getCompany().getId())
                .companyName(job.getCompany().getName())
                .companyLogo(job.getCompany().getLogoUrl())
                .build();
    }
}
