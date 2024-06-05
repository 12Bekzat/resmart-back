package com.app.resmart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.resmart.dto.ProductDto;
import com.app.resmart.dto.ReviewDto;
import com.app.resmart.entity.Product;
import com.app.resmart.entity.User;
import com.app.resmart.error.AppError;
import com.app.resmart.repository.ProductRepo;
import com.app.resmart.repository.ReviewRepo;
import com.app.resmart.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductController {
    private final UserService userService;
    private final ProductRepo productRepo;
    private final ReviewRepo reviewRepo;

    @GetMapping("/products")
    public ResponseEntity<?> getProducts() {
        List<Product> products = productRepo.findAll();

        List<ProductDto> productDtos = products.stream().map((item) -> {
            return new ProductDto(item.getId(), item.getName(),
                    item.getPrice(), item.getDiscount(), item.getExpiredDate(), item.getCount(), item.getCategory(),
                    item.getImageUrl(), item.isDeleted(),
                    null);
        }).toList();

        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/users/{id}/products")
    public ResponseEntity<?> getUserProducts(@PathVariable(name = "id") Long id) {
        User user = userService.findById(id).get();

        List<ProductDto> productDtos = user.getProducts().stream().map((item) -> {
            return new ProductDto(item.getId(), item.getName(),
                    item.getPrice(), item.getDiscount(), item.getExpiredDate(), item.getCount(), item.getCategory(),
                    item.getImageUrl(), item.isDeleted(),
                    null);
        }).toList();

        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProduct(@PathVariable(name = "id") Long id) {
        Product item = productRepo.findById(id).get();

        ProductDto productDto = new ProductDto(item.getId(), item.getName(),
                item.getPrice(), item.getDiscount(), item.getExpiredDate(), item.getCount(), item.getCategory(),
                item.getImageUrl(), item.isDeleted(),
                item.getReviews().stream().map((review) -> {
                    return new ReviewDto(review.getId(), review.getComment(), review.getRate(),
                            review.getUser().getId(), review.getUser().getUsername());
                }).toList());

        return ResponseEntity.ok(productDto);
    }

    @PostMapping("/users/{id}/product/create")
    public ResponseEntity<?> createProduct(@PathVariable(name = "id") Long id, @RequestBody ProductDto productDto) {
        User user = userService.findById(id).get();

        Product product = new Product();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setDiscount(productDto.getDiscount());
        product.setDeleted(false);
        product.setExpiredDate(productDto.getExpiredDate());
        product.setCount(productDto.getCount());
        product.setCategory(productDto.getCategory());
        product.setImageUrl(productDto.getImageUrl());
        product.setSupplier(user);

        user.addProduct(product);
        userService.saveUser(user);

        return ResponseEntity.ok(new AppError(200, "Successfully added!"));
    }

    @PostMapping("/products/{pId}/remove")
    public ResponseEntity<?> removeProduct(@PathVariable Long pId) {
        Product product = productRepo.findById(pId).get();

        product.setDeleted(true);

        productRepo.save(product);

        return ResponseEntity.ok(new AppError(200, "Successfully added!"));
    }

    @PostMapping("/products/{pId}/edit")
    public ResponseEntity<?> editProduct(@PathVariable Long pId, @RequestBody ProductDto productDto) {
        Product product = productRepo.findById(pId).get();

        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setDiscount(productDto.getDiscount());
        product.setExpiredDate(productDto.getExpiredDate());
        product.setCount(productDto.getCount());
        product.setCategory(productDto.getCategory());
        product.setImageUrl(productDto.getImageUrl());

        productRepo.save(product);
        
        return ResponseEntity.ok(new AppError(200, "Successfully added!"));
    }
}
