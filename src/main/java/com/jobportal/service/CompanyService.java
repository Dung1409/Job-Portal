package com.jobportal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.dto.request.CompanyRequest;
import com.jobportal.dto.response.CompanyResponse;
import com.jobportal.entity.Company;
import com.jobportal.entity.User;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepo;

    public List<CompanyResponse> getAll() {
        return companyRepo.findAll().stream().map(this::toResponse).toList();
    }

    public CompanyResponse getById(Long id) {
        Company company = companyRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return toResponse(company);
    }

    public CompanyResponse getMyCompany(User recruiter) {
        Company company = companyRepo.findByRecruiterId(recruiter.getId())
                .orElseThrow(() -> new ResourceNotFoundException("You haven't created a company yet"));
        return toResponse(company);
    }

    @Transactional
    public CompanyResponse create(CompanyRequest req, User recruiter) {
        if (companyRepo.existsByRecruiterId(recruiter.getId())) {
            throw new DuplicateResourceException("You already have a company");
        }

        Company company = Company.builder()
                .recruiter(recruiter)
                .name(req.getName())
                .description(req.getDescription())
                .website(req.getWebsite())
                .address(req.getAddress())
                .industry(req.getIndustry())
                .companySize(req.getCompanySize())
                .isVerified(false)
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(companyRepo.save(company));
    }

    @Transactional
    public CompanyResponse update(Long id, CompanyRequest req, User recruiter) {
        Company company = companyRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (!company.getRecruiter().getId().equals(recruiter.getId())) {
            throw new UnauthorizedException("You are not authorized to update this company");
        }

        if (req.getName() != null) company.setName(req.getName());
        if (req.getDescription() != null) company.setDescription(req.getDescription());
        if (req.getWebsite() != null) company.setWebsite(req.getWebsite());
        if (req.getAddress() != null) company.setAddress(req.getAddress());
        if (req.getIndustry() != null) company.setIndustry(req.getIndustry());
        if (req.getCompanySize() != null) company.setCompanySize(req.getCompanySize());

        return toResponse(companyRepo.save(company));
    }

    @Transactional
    public void delete(Long id, User user) {
        Company company = companyRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        if (!company.getRecruiter().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this company");
        }
        companyRepo.delete(company);
    }

    @Transactional
    public void verify(Long id, boolean verified) {
        Company company = companyRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        company.setVerified(verified);
        companyRepo.save(company);
    }

    private CompanyResponse toResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .website(company.getWebsite())
                .logoUrl(company.getLogoUrl())
                .address(company.getAddress())
                .industry(company.getIndustry())
                .companySize(company.getCompanySize())
                .isVerified(company.isVerified())
                .recruiterId(company.getRecruiter().getId())
                .recruiterName(company.getRecruiter().getFullName())
                .build();
    }
}
