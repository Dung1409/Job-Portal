package com.jobportal.dto.response;

import java.time.LocalDateTime;

import com.jobportal.enums.ApplicationStatus;

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
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long applicantId;
    private String applicantName;
    private String applicantEmail;
    private String coverLetter;
    private String resumeUrl;
    private ApplicationStatus status;
    private String note;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
