package com.jobportal.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.entity.Category;
import com.jobportal.exception.DuplicateResourceException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepo;

    public List<Category> getAll() {
        return categoryRepo.findByIsActiveTrue();
    }

    public List<Category> getAllAdmin() {
        return categoryRepo.findAll();
    }

    @Transactional
    public Category create(String name, String icon) {
        if (categoryRepo.existsByName(name)) {
            throw new DuplicateResourceException("Category already exists");
        }
        return categoryRepo.save(Category.builder()
                .name(name)
                .icon(icon)
                .isActive(true)
                .build());
    }

    @Transactional
    public Category update(Long id, String name, String icon, Boolean isActive) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (name != null) category.setName(name);
        if (icon != null) category.setIcon(icon);
        if (isActive != null) category.setActive(isActive);
        return categoryRepo.save(category);
    }

    @Transactional
    public void delete(Long id) {
        if (!categoryRepo.existsById(id)) {
            throw new ResourceNotFoundException("Category not found");
        }
        categoryRepo.deleteById(id);
    }
}
