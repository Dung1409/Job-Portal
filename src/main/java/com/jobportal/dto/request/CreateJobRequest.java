package com.jobportal.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.jobportal.enums.ExperienceLevel;
import com.jobportal.enums.JobType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class CreateJobRequest {
    @NotBlank
    @Size(max = 300)
    private String title;

    private Long categoryId;

    @NotBlank
    private String description;

    private String requirements;
    private String benefits;
    private String location;

    @Positive
    private BigDecimal salaryMin;

    @Positive
    private BigDecimal salaryMax;

    private String currency;
    private JobType jobType;
    private ExperienceLevel experience;
    private LocalDate deadline;
}
