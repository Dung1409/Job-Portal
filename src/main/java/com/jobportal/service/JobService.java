package com.jobportal.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.dto.request.CreateJobRequest;
import com.jobportal.dto.request.UpdateJobRequest;
import com.jobportal.dto.response.JobResponse;
import com.jobportal.entity.Category;
import com.jobportal.entity.Company;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.enums.JobStatus;
import com.jobportal.enums.Role;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.CategoryRepository;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.repository.JobRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepo;
    private final CompanyRepository companyRepo;
    private final CategoryRepository categoryRepo;
    private final ApplicationRepository appRepo;

    public Page<JobResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jobRepo.findByStatus(JobStatus.OPEN, pageable).map(this::toResponse);
    }

    public Page<JobResponse> search(String keyword, String location, Long categoryId,
                                     String jobType, String experience,
                                     Double salaryMin, Double salaryMax,
                                     int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return jobRepo.searchJobs(keyword, location, categoryId, jobType, experience,
                        salaryMin, salaryMax, pageable)
                .map(this::toResponse);
    }

    public JobResponse getById(Long id) {
        Job job = jobRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        job.setViews(job.getViews() + 1);
        jobRepo.save(job);
        return toResponse(job);
    }

    public Page<JobResponse> getMyJobs(User recruiter, int page, int size) {
        Company company = companyRepo.findByRecruiterId(recruiter.getId())
                .orElseThrow(() -> new ResourceNotFoundException("You need to create a company first"));
        Pageable pageable = PageRequest.of(page, size);
        return jobRepo.findByCompanyId(company.getId(), pageable).map(this::toResponse);
    }

    @Transactional
    public JobResponse create(CreateJobRequest req, User recruiter) {
        Company company = companyRepo.findByRecruiterId(recruiter.getId())
                .orElseThrow(() -> new ResourceNotFoundException("You need to create a company first"));

        Job.JobBuilder builder = Job.builder()
                .company(company)
                .title(req.getTitle())
                .description(req.getDescription())
                .requirements(req.getRequirements())
                .benefits(req.getBenefits())
                .location(req.getLocation())
                .salaryMin(req.getSalaryMin())
                .salaryMax(req.getSalaryMax())
                .currency(req.getCurrency())
                .jobType(req.getJobType())
                .experience(req.getExperience())
                .status(JobStatus.OPEN)
                .deadline(req.getDeadline())
                .createdAt(LocalDateTime.now());

        if (req.getCategoryId() != null) {
            Category category = categoryRepo.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            builder.category(category);
        }

        return toResponse(jobRepo.save(builder.build()));
    }

    @Transactional
    public JobResponse update(Long id, UpdateJobRequest req, User user) {
        Job job = jobRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        checkOwnership(job, user);

        if (req.getTitle() != null) job.setTitle(req.getTitle());
        if (req.getDescription() != null) job.setDescription(req.getDescription());
        if (req.getRequirements() != null) job.setRequirements(req.getRequirements());
        if (req.getBenefits() != null) job.setBenefits(req.getBenefits());
        if (req.getLocation() != null) job.setLocation(req.getLocation());
        if (req.getSalaryMin() != null) job.setSalaryMin(req.getSalaryMin());
        if (req.getSalaryMax() != null) job.setSalaryMax(req.getSalaryMax());
        if (req.getCurrency() != null) job.setCurrency(req.getCurrency());
        if (req.getJobType() != null) job.setJobType(req.getJobType());
        if (req.getExperience() != null) job.setExperience(req.getExperience());
        if (req.getStatus() != null) job.setStatus(req.getStatus());
        if (req.getDeadline() != null) job.setDeadline(req.getDeadline());
        if (req.getCategoryId() != null) {
            Category category = categoryRepo.findById(req.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            job.setCategory(category);
        }
        job.setUpdatedAt(LocalDateTime.now());

        return toResponse(jobRepo.save(job));
    }

    @Transactional
    public void updateStatus(Long id, JobStatus status, User user) {
        Job job = jobRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        if (user.getRole() == Role.RECRUITER) {
            checkOwnership(job, user);
        }
        job.setStatus(status);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepo.save(job);
    }

    @Transactional
    public void delete(Long id, User user) {
        Job job = jobRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));
        if (user.getRole() == Role.RECRUITER) {
            checkOwnership(job, user);
        }
        jobRepo.delete(job);
    }

    private void checkOwnership(Job job, User user) {
        if (!job.getCompany().getRecruiter().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to modify this job");
        }
    }

    private JobResponse toResponse(Job job) {
        int count = (int) appRepo.countByJobId(job.getId());
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .requirements(job.getRequirements())
                .benefits(job.getBenefits())
                .location(job.getLocation())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .currency(job.getCurrency())
                .jobType(job.getJobType())
                .experience(job.getExperience())
                .status(job.getStatus())
                .deadline(job.getDeadline())
                .views(job.getViews())
                .companyId(job.getCompany().getId())
                .companyName(job.getCompany().getName())
                .companyLogo(job.getCompany().getLogoUrl())
                .categoryId(job.getCategory() != null ? job.getCategory().getId() : null)
                .categoryName(job.getCategory() != null ? job.getCategory().getName() : null)
                .applicantCount(count)
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
