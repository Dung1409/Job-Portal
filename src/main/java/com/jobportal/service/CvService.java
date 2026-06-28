package com.jobportal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.dto.response.CvResponse;
import com.jobportal.entity.Cv;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.exception.UnauthorizedException;
import com.jobportal.repository.CvRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CvService {

    private final CvRepository cvRepo;
    private final FileStorageService fileStorageService;

    @Transactional
    public CvResponse upload(Long userId, MultipartFile file) {
        String storedName = fileStorageService.store(file);
        Cv cv = Cv.builder()
                .userId(userId)
                .originalFilename(file.getOriginalFilename())
                .storedFilename(storedName)
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .uploadedAt(LocalDateTime.now())
                .build();
        return toResponse(cvRepo.save(cv));
    }

    public List<CvResponse> getCvsByUser(Long userId) {
        return cvRepo.findByUserIdOrderByUploadedAtDesc(userId)
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long userId, Long cvId) {
        Cv cv = cvRepo.findById(cvId)
                .orElseThrow(() -> new ResourceNotFoundException("CV not found"));
        if (!cv.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this CV");
        }
        fileStorageService.delete(cv.getStoredFilename());
        cvRepo.delete(cv);
    }

    public Cv getCvById(Long cvId) {
        return cvRepo.findById(cvId)
                .orElseThrow(() -> new ResourceNotFoundException("CV not found"));
    }

    private CvResponse toResponse(Cv cv) {
        return CvResponse.builder()
                .id(cv.getId())
                .originalFilename(cv.getOriginalFilename())
                .fileSize(cv.getFileSize())
                .contentType(cv.getContentType())
                .uploadedAt(cv.getUploadedAt())
                .build();
    }
}
