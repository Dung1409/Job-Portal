package com.jobportal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jobportal.entity.Job;
import com.jobportal.enums.JobStatus;

public interface JobRepository extends JpaRepository<Job, Long> {
    Page<Job> findByCompanyId(Long companyId, Pageable pageable);

    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE " +
           "(:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:categoryId IS NULL OR j.category.id = :categoryId) AND " +
           "(:jobType IS NULL OR j.jobType = :jobType) AND " +
           "(:experience IS NULL OR j.experience = :experience) AND " +
           "(:salaryMin IS NULL OR j.salaryMax >= :salaryMin) AND " +
           "(:salaryMax IS NULL OR j.salaryMin <= :salaryMax) AND " +
           "j.status = 'OPEN'")
    Page<Job> searchJobs(@Param("keyword") String keyword,
                         @Param("location") String location,
                         @Param("categoryId") Long categoryId,
                         @Param("jobType") String jobType,
                         @Param("experience") String experience,
                         @Param("salaryMin") Double salaryMin,
                         @Param("salaryMax") Double salaryMax,
                         Pageable pageable);

    List<Job> findByCompanyId(Long companyId);

    long countByCompanyIdAndStatus(Long companyId, JobStatus status);
}
