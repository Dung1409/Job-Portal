package com.jobportal.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.response.ApiResponse;
import com.jobportal.entity.Category;
import com.jobportal.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(categoryService.getAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Category>> create(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.created(
                categoryService.create(body.get("name"), body.get("icon"))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> update(@PathVariable Long id,
                                                         @RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String icon = (String) body.get("icon");
        Boolean isActive = body.get("isActive") != null ? (Boolean) body.get("isActive") : null;
        return ResponseEntity.ok(ApiResponse.ok(categoryService.update(id, name, icon, isActive)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
