package com.app.resmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.resmart.entity.Review;

public interface ReviewRepo extends JpaRepository<Review, Long> {

}
