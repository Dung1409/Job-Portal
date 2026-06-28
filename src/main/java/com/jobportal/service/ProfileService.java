package com.jobportal.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.dto.request.UpdateProfileRequest;
import com.jobportal.entity.ApplicantProfile;
import com.jobportal.entity.User;
import com.jobportal.repository.ApplicantProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ApplicantProfileRepository profileRepo;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public ApplicantProfile getProfile(Long userId) {
        return profileRepo.findByUserId(userId)
                .orElse(null);
    }

    @Transactional
    public ApplicantProfile updateProfile(Long userId, UpdateProfileRequest req) {
        ApplicantProfile profile = profileRepo.findByUserId(userId)
                .orElse(null);

        if (profile == null) {
            User user = new User();
            user.setId(userId);
            profile = ApplicantProfile.builder()
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        if (req.getHeadline() != null) profile.setHeadline(req.getHeadline());
        if (req.getSummary() != null) profile.setSummary(req.getSummary());
        if (req.getSkills() != null) profile.setSkills(req.getSkills());
        if (req.getLinkedinUrl() != null) profile.setLinkedinUrl(req.getLinkedinUrl());
        if (req.getGithubUrl() != null) profile.setGithubUrl(req.getGithubUrl());
        if (req.getDateOfBirth() != null) profile.setDateOfBirth(req.getDateOfBirth());
        if (req.getGender() != null) profile.setGender(req.getGender());
        if (req.getAddress() != null) profile.setAddress(req.getAddress());

        return profileRepo.save(profile);
    }

    @Transactional
    public ApplicantProfile updateResume(Long userId, String resumeUrl) {
        ApplicantProfile profile = profileRepo.findByUserId(userId)
                .orElse(null);

        if (profile == null) {
            User user = new User();
            user.setId(userId);
            profile = ApplicantProfile.builder()
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        if (profile.getResumeUrl() != null) {
            fileStorageService.delete(profile.getResumeUrl());
        }

        profile.setResumeUrl(resumeUrl);
        return profileRepo.save(profile);
    }

    @Transactional
    public ApplicantProfile updateAvatar(Long userId, String avatarUrl) {
        ApplicantProfile profile = profileRepo.findByUserId(userId)
                .orElse(null);

        if (profile == null) {
            User user = new User();
            user.setId(userId);
            profile = ApplicantProfile.builder()
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        return profileRepo.save(profile);
    }
}
