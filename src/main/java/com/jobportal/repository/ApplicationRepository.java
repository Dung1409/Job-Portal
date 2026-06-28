package com.jobportal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jobportal.entity.Application;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByApplicantId(Long applicantId);
    Page<Application> findByApplicantId(Long applicantId, Pageable pageable);
    List<Application> findByJobId(Long jobId);
    Page<Application> findByJobId(Long jobId, Pageable pageable);
    boolean existsByJobIdAndApplicantId(Long jobId, Long applicantId);
    long countByJobId(Long jobId);
}
