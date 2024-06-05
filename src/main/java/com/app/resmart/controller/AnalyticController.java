package com.app.resmart.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.resmart.dto.AnalyticPeriod;
import com.app.resmart.dto.DateDto;
import com.app.resmart.dto.OrderItemDto;
import com.app.resmart.entity.OrderItem;
import com.app.resmart.entity.Product;
import com.app.resmart.entity.User;
import com.app.resmart.error.AppError;
import com.app.resmart.repository.OrderItemRepo;
import com.app.resmart.repository.OrderRepo;
import com.app.resmart.repository.ProductRepo;
import com.app.resmart.service.UserService;

import jakarta.mail.internet.ParseException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AnalyticController {
    private final UserService userService;
    private final OrderItemRepo orderItemRepo;
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;

    @GetMapping("/analytic/all")
    public ResponseEntity<?> getAllAnalytic() throws java.text.ParseException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(String.valueOf(authentication.getPrincipal())).get();

            List<OrderItem> orderItems = orderItemRepo.findAll();
            orderItems = orderItems.stream()
                    .filter((item) -> {
                        return item.getSupplier().getId().equals(user.getId());
                    }).toList();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minus(1, ChronoUnit.YEARS);

            orderItems = orderItems.stream().filter((item) -> {
                LocalDate dateToCheck = LocalDate.parse(item.getOrderDate(), formatter);
                System.out.println(item.getId() + " " + dateToCheck + " "
                        + (dateToCheck.isBefore(endDate) && dateToCheck.isAfter(startDate)));
                return dateToCheck.isBefore(endDate) && dateToCheck.isAfter(startDate);
            }).toList();

            System.out.println("\nMEGA SIZE = " + orderItems.size());
            System.out.println();

            Map<Month, Integer> ordersCount = new HashMap<>();
            Map<Month, Float> ordersMoney = new HashMap<>();
            Map<Month, List<OrderItem>> ordersCountOfClients = new HashMap<>();

            for (OrderItem orderItem : orderItems) {
                LocalDate dateToCheck = LocalDate.parse(orderItem.getOrderDate(), formatter);
                Month month = dateToCheck.getMonth();
                ordersCount.put(month, ordersCount.getOrDefault(month, 0) + 1);
                ordersMoney.put(month, ordersMoney.getOrDefault(month, 0f) + orderItem.getTotalPrice());
                List<OrderItem> od = ordersCountOfClients.get(month);
                if (od == null)
                    od = new ArrayList<>();
                od.add(orderItem);
                ordersCountOfClients.put(month, ordersCountOfClients.getOrDefault(month, od));
            }

            Map<Month, Integer> orderClient = new HashMap<>();

            for (Map.Entry<Month, List<OrderItem>> entry : ordersCountOfClients.entrySet()) {
                Map<Long, Integer> clients = new HashMap<>();

                for (OrderItem orderItem : entry.getValue()) {
                    Long rId = orderItem.getRestaurant().getId();
                    clients.put(rId, clients.getOrDefault(rId, 0) + 1);
                }

                orderClient.put(entry.getKey(), clients.size());
            }

            return ResponseEntity.ok(new AnalyticPeriod(ordersCount, ordersMoney, orderClient));
        }

        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized user!"),
                HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/analytic/products")
    public ResponseEntity<?> getAnalyticProducts() throws java.text.ParseException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(String.valueOf(authentication.getPrincipal())).get();

            List<OrderItem> orderItems = orderItemRepo.findAll();
            orderItems = orderItems.stream()
                    .filter((item) -> {
                        return item.getSupplier().getId().equals(user.getId());
                    }).toList();

            Map<String, Integer> products = new HashMap<>();

            for (OrderItem orderItem : orderItems) {
                Product pr = orderItem.getProduct();
                int quantity = orderItem.getQuantity();
                products.put(pr.getName(), products.getOrDefault(pr.getName(), 0) + quantity);
            }

            return ResponseEntity.ok(products);
        }

        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized user!"),
                HttpStatus.UNAUTHORIZED);
    }
}
