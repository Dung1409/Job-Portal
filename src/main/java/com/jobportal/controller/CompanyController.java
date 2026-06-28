package com.jobportal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.request.CompanyRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.CompanyResponse;
import com.jobportal.entity.User;
import com.jobportal.service.CompanyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(companyService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.getById(id)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CompanyResponse>> getMyCompany(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.getMyCompany(user)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> create(@RequestBody CompanyRequest req,
                                                                @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.created(companyService.create(req, user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> update(@PathVariable Long id,
                                                                @RequestBody CompanyRequest req,
                                                                @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.update(id, req, user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                     @AuthenticationPrincipal User user) {
        companyService.delete(id, user);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
