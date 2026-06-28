package com.jobportal.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

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
public class UpdateJobRequest {
    private String title;
    private Long categoryId;
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
}
