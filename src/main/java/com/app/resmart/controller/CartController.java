package com.app.resmart.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.resmart.dto.CartDto;
import com.app.resmart.dto.OrderDto;
import com.app.resmart.dto.OrderItemDto;
import com.app.resmart.entity.Cart;
import com.app.resmart.entity.Order;
import com.app.resmart.entity.OrderItem;
import com.app.resmart.entity.OrderStatus;
import com.app.resmart.entity.Product;
import com.app.resmart.entity.User;
import com.app.resmart.error.AppError;
import com.app.resmart.repository.CartRepo;
import com.app.resmart.repository.OrderItemRepo;
import com.app.resmart.repository.OrderRepo;
import com.app.resmart.repository.ProductRepo;
import com.app.resmart.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartRepo cartRepo;
    private final UserService userService;
    private final OrderItemRepo orderItemRepo;
    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;

    @GetMapping("/cart")
    public ResponseEntity<?> getCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(String.valueOf(authentication.getPrincipal())).get();
            Cart cart = user.getCart();

            System.out.println("Cart Username: " + user.getUsername());

            if (cart == null)
                return ResponseEntity.ok(new AppError(HttpStatus.NOT_FOUND.value(), "Cart does not exist!"));
            else
                return ResponseEntity.ok(new CartDto(cart.getId(), cart.getTotalPrice(), cart.getOrders()));
        }

        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized user!"),
                HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getOrdersToPost() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(String.valueOf(authentication.getPrincipal())).get();

            List<OrderItem> orderItems = orderItemRepo.findAll();
            List<OrderItemDto> orderItemDtos = orderItems.stream()
                    .filter((item) -> {
                        return item.getSupplier().getId().equals(user.getId());
                    })
                    .map((item) -> {
                        return new OrderItemDto(item.getId(),
                                item.getQuantity(),
                                item.getPrice(),
                                item.getTotalPrice(),
                                item.getProduct(),
                                item.getSupplier().getId(),
                                item.getRestaurant() != null ? item.getRestaurant().getId() : -1,
                                item.getStatus(),
                                item.getPayStatus(),
                                item.getPayWay(),
                                item.getOrderDate(),
                                item.getDeliveryDate());
                    }).toList();

            return ResponseEntity.ok(orderItemDtos);
        }

        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized user!"),
                HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/order/{id}/remove")
    public ResponseEntity<?> removeOrder(@PathVariable(name = "id") Long id) {
        OrderItem orderItem = orderItemRepo.findById(id).get();
        Order order = orderItemRepo.findById(id).get().getOrder();

        order.removeOrderItem(orderItem);
        orderRepo.save(order);

        orderItemRepo.deleteById(id);

        return ResponseEntity.ok(new AppError(200, "deleted success"));
    }

    @PostMapping("/order/{id}/edit")
    public ResponseEntity<?> editOrder(@PathVariable(name = "id") Long id, @RequestBody OrderStatus status) {
        OrderItem orderItem = orderItemRepo.findById(id).get();

        orderItem.setStatus(status);

        orderItemRepo.save(orderItem);

        return ResponseEntity.ok(new AppError(200, "deleted success"));
    }

    @PostMapping("/order/{id}/pay/edit")
    public ResponseEntity<?> editPayOrder(@PathVariable(name = "id") Long id, @RequestBody OrderStatus status) {
        OrderItem orderItem = orderItemRepo.findById(id).get();

        orderItem.setPayStatus(status);

        orderItemRepo.save(orderItem);

        return ResponseEntity.ok(new AppError(200, "deleted success"));
    }

    @PostMapping("/cart/create")
    public ResponseEntity<?> createCart(@RequestBody OrderDto orderDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(String.valueOf(authentication.getPrincipal())).get();

            if (user.getCart() != null)
                return new ResponseEntity<>(
                        new AppError(HttpStatus.BAD_REQUEST.value(), "Cannot create cart, it is already exist!"),
                        HttpStatus.BAD_REQUEST);

            Cart cart = new Cart();
            Order order = new Order();

            List<OrderItem> orderItems = orderDto.getOrderItems().stream().map((item) -> {
                OrderItem orderItem = new OrderItem();
                Product product = productRepo.findById(item.getProduct().getId()).get();
                int newCount = product.getCount() - item.getQuantity();
                product.setCount(newCount);

                if (newCount >= 0) {
                    productRepo.save(product);
                } else {
                    return null;
                }

                orderItem.setQuantity(item.getQuantity());
                orderItem.setDeliveryDate(item.getDeliveryDate());
                orderItem.setOrderDate(item.getOrderDate());
                orderItem.setStatus(OrderStatus.WAIT);
                orderItem.setPayStatus(OrderStatus.NOPAYED);
                orderItem.setPayWay(item.getPayWay());
                orderItem.setPrice(item.getPrice());
                orderItem.setTotalPrice(item.getPrice() * item.getQuantity());
                orderItem.setProduct(product);
                orderItem.setSupplier(product.getSupplier());
                orderItem.setRestaurant(user);
                orderItem.setOrder(order);

                return orderItem;
            }).toList();

            float totalPrice = 0;
            for (OrderItem orderItem : orderItems) {
                totalPrice += orderItem.getTotalPrice();
            }
            order.setTotalPrice(totalPrice);

            if (orderItems.stream().filter((item) -> {
                return item == null;
            }).toList().size() > 0) {
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
                        "Quantity more than count of product!"),
                        HttpStatus.BAD_REQUEST);
            }

            order.setOrderItems(orderItems);
            order.setRestaurant(user);
            cart.addOrder(order);
            user.setCart(cart);

            userService.saveUser(user);

            // return ResponseEntity.ok(new AppError(200, "ok!"));
            return ResponseEntity.ok(new CartDto(cart.getId(), cart.getTotalPrice(), cart.getOrders()));
        }

        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized user!"),
                HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/cart/add")
    public ResponseEntity<?> addOrder(@RequestBody OrderDto orderDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(String.valueOf(authentication.getPrincipal())).get();
            Cart cart = user.getCart();

            if (cart == null)
                cart = new Cart();

            Order order = new Order();

            List<OrderItem> orderItems = orderDto.getOrderItems().stream().map((item) -> {
                System.out.println(item);
                OrderItem orderItem = new OrderItem();
                Product product = productRepo.findById(item.getProduct().getId()).get();
                int newCount = product.getCount() - item.getQuantity();

                if (newCount >= 0) {
                    product.setCount(newCount);
                    productRepo.save(product);
                } else {
                    return null;
                }

                orderItem.setQuantity(item.getQuantity());
                orderItem.setDeliveryDate(item.getDeliveryDate());
                orderItem.setOrderDate(item.getOrderDate());
                orderItem.setStatus(OrderStatus.WAIT);
                orderItem.setPayStatus(OrderStatus.NOPAYED);
                orderItem.setPayWay(item.getPayWay());
                orderItem.setPrice(item.getPrice());
                orderItem.setTotalPrice(item.getPrice() * item.getQuantity());
                orderItem.setProduct(product);
                orderItem.setSupplier(product.getSupplier());
                orderItem.setRestaurant(user);
                orderItem.setOrder(order);

                return orderItem;
            }).toList();

            if (orderItems.stream().filter((item) -> {
                return item == null;
            }).toList().size() > 0) {
                return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
                        "Quantity more than count of product!"),
                        HttpStatus.BAD_REQUEST);
            }
            float totalPrice = 0;
            for (OrderItem orderItem : orderItems) {
                totalPrice += orderItem.getTotalPrice();
            }
            order.setTotalPrice(totalPrice);

            order.setOrderItems(orderItems);
            order.setRestaurant(user);
            cart.addOrder(order);

            if (user.getCart() == null) {
                user.setCart(cart);
                userService.saveUser(user);

            } else {
                cartRepo.save(cart);
            }

            return ResponseEntity.ok(new CartDto(cart.getId(), cart.getTotalPrice(), cart.getOrders()));
        }

        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized user!"),
                HttpStatus.UNAUTHORIZED);
    }
}
