package com.jobportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobportal.entity.Cv;

public interface CvRepository extends JpaRepository<Cv, Long> {
    List<Cv> findByUserIdOrderByUploadedAtDesc(Long userId);
    Optional<Cv> findByIdAndUserId(Long id, Long userId);
    long countByUserId(Long userId);
}
