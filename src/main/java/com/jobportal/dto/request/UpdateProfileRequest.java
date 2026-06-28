package com.jobportal.dto.request;

import java.time.LocalDate;

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
public class UpdateProfileRequest {
    private String headline;
    private String summary;
    private String skills;
    private String linkedinUrl;
    private String githubUrl;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String phone;
}
