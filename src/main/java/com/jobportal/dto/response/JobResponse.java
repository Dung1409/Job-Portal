package com.jobportal.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.jobportal.enums.ExperienceLevel;
import com.jobportal.enums.JobStatus;
import com.jobportal.enums.JobType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String requirements;
    private String benefits;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String currency;
    private JobType jobType;
    private ExperienceLevel experience;
    private JobStatus status;
    private LocalDate deadline;
    private int views;
    private Long companyId;
    private String companyName;
    private String companyLogo;
    private Long categoryId;
    private String categoryName;
    private int applicantCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
