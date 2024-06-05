package com.app.resmart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.resmart.entity.Category;
import com.app.resmart.error.AppError;
import com.app.resmart.repository.CategoryRepo;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryRepo categoryRepo;

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        List<Category> categories = categoryRepo.findAll();

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<?> getCategory(@PathVariable(name = "id") Long id) {
        Category category = categoryRepo.findById(id).get();

        return ResponseEntity.ok(category);
    }

    @PostMapping("/categories/create")
    public ResponseEntity<?> getCategory(@RequestBody Category category) {
        categoryRepo.save(category);

        return ResponseEntity.ok(category);
    }

    @PostMapping("/categories/{id}/remove")
    public ResponseEntity<?> removeCategory(@PathVariable(name = "id") Long id) {
        categoryRepo.deleteById(id);

        return ResponseEntity.ok(new AppError(200, "removed!"));
    }
}
