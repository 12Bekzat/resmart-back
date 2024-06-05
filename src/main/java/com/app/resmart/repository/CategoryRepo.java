package com.app.resmart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.resmart.entity.Category;

public interface CategoryRepo extends JpaRepository<Category, Long>{
    Optional<Category> findByName(String name);
}
