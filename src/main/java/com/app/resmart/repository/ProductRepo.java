package com.app.resmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.resmart.entity.Product;

public interface ProductRepo extends JpaRepository<Product, Long>{

}
