package com.jobportal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jobportal.dto.response.UserResponse;
import com.jobportal.entity.User;
import com.jobportal.enums.Role;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepo;
    private final JobRepository jobRepo;
    private final ApplicationRepository appRepo;
    private final CompanyRepository companyRepo;

    public record SystemStats(
        long totalUsers, long totalRecruiters, long totalApplicants,
        long totalJobs, long openJobs,
        long totalApplications, long totalCompanies
    ) {}

    public SystemStats getStats() {
        List<User> allUsers = userRepo.findAll();
        return new SystemStats(
                allUsers.size(),
                allUsers.stream().filter(u -> u.getRole() == Role.RECRUITER).count(),
                allUsers.stream().filter(u -> u.getRole() == Role.APPLICANT).count(),
                jobRepo.count(),
                jobRepo.findAll().stream().filter(j -> j.getStatus().name().equals("OPEN")).count(),
                appRepo.count(),
                companyRepo.count()
        );
    }

    public List<UserResponse> getAllUsers() {
        return userRepo.findAll().stream().map(this::toUserResponse).toList();
    }

    public void toggleUserActive(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(!user.isActive());
        userRepo.save(user);
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .isActive(user.isActive())
                .build();
    }
}
