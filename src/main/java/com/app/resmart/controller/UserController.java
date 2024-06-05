package com.app.resmart.controller;

import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.resmart.dto.CartDto;
import com.app.resmart.dto.JwtRequest;
import com.app.resmart.dto.JwtResponse;
import com.app.resmart.dto.ProductDto;
import com.app.resmart.dto.RegUserDto;
import com.app.resmart.dto.ReviewDto;
import com.app.resmart.dto.UserDto;
import com.app.resmart.entity.User;
import com.app.resmart.error.AppError;
import com.app.resmart.service.UserService;
import com.app.resmart.utils.JwtTokenUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest jwtRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Incorrect login or password"),
                    HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(jwtRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegUserDto regUserDto) {
        if (userService.findByUsername(regUserDto.getUsername()).isPresent()) {
            System.out.println("this");
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "User is already exist!"),
                    HttpStatus.BAD_REQUEST);
        }

        userService.createNewUser(regUserDto);

        return ResponseEntity.ok(new AppError(200, "success reg!"));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMy() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(String.valueOf(authentication.getPrincipal())).get();
            return ResponseEntity.ok(new UserDto(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.isBanned(),
                    user.getContactPerson(),
                    user.getPhone(),
                    user.getName(),
                    user.getAddress(),
                    user.getDescText(),
                    user.getLogotype(),
                    user.getWorkTime(),
                    user.getCreatedAt(),
                    null, user.getRoles().toString()));
        }

        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized"),
                HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(String.valueOf(authentication.getPrincipal())).get();
            return ResponseEntity.ok(user.getRoles());
        }

        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized user!"),
                HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editMe(@RequestBody UserDto editDto) {
        System.out.println(editDto);
        Optional<User> byUsername = userService.findByUsername(String.valueOf(editDto.getUsername()));

        if (byUsername.isPresent()) {
            User user = byUsername.get();
            user.setName(editDto.getName());
            user.setContactPerson(editDto.getContactPerson());
            user.setPhone(editDto.getPhone());
            user.setPassword(editDto.getPassword());
            user.setLogotype(editDto.getLogotype());
            user.setEmail(editDto.getEmail());
            user.setWorkTime(editDto.getWorkTime());
            user.setAddress(editDto.getAddress());

            userService.saveUserWithEncrypt(user);

            return ResponseEntity.ok(new AppError(200, "Ok!"));
        }

        return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "User not found!"),
                HttpStatus.UNAUTHORIZED);

    }

    @PostMapping("/users/{id}/edit")
    public ResponseEntity<?> editUser(@PathVariable(name = "id") Long id, @RequestBody UserDto editDto) {
        Optional<User> byUsername = userService.findById(id);

        if (byUsername.isPresent()) {
            User user = byUsername.get();
            user.setName(editDto.getName());
            user.setContactPerson(editDto.getContactPerson());
            user.setPhone(editDto.getPhone());
            user.setLogotype(editDto.getLogotype());
            user.setWorkTime(editDto.getWorkTime());
            user.setAddress(editDto.getAddress());
            user.setDescText(editDto.getDescText());

            userService.saveUser(user);

            return ResponseEntity.ok(new AppError(200, "Ok!"));
        }

        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "User not found!"),
                HttpStatus.UNAUTHORIZED);

    }

    @PostMapping("/users/{id}/remove")
    public ResponseEntity<?> removeUser(@PathVariable(name = "id") Long id) {
        userService.removeUser(id);
        return ResponseEntity.ok(new AppError(200, "Ok!"));
    }

    @PostMapping("/users/{id}/access")
    public ResponseEntity<?> accessUser(@PathVariable(name = "id") Long id) {
        Optional<User> byUsername = userService.findById(id);

        if (byUsername.isPresent()) {
            User user = byUsername.get();
            user.setBanned(false);

            userService.saveUser(user);

            return ResponseEntity.ok(new AppError(200, "Ok!"));
        }

        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "User not found!"),
                HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/users/{id}/unaccess")
    public ResponseEntity<?> unaccessUser(@PathVariable(name = "id") Long id) {
        Optional<User> byUsername = userService.findById(id);

        if (byUsername.isPresent()) {
            User user = byUsername.get();
            user.setBanned(true);

            userService.saveUser(user);

            return ResponseEntity.ok(new AppError(200, "Ok!"));
        }

        return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "User not found!"),
                HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        List<User> users = userService.findAll();

        List<UserDto> userDtos = users.stream()
                .filter((item) -> {
                    return !item.getUsername().equals("admin");
                }).map((item) -> {
                    return new UserDto(
                            item.getId(),
                            item.getUsername(),
                            item.getPassword(),
                            item.getEmail(),
                            item.isBanned(),
                            item.getContactPerson(),
                            item.getPhone(),
                            item.getName(),
                            item.getAddress(),
                            item.getDescText(),
                            item.getLogotype(),
                            item.getWorkTime(),
                            item.getCreatedAt(),
                            null, item.getRoles().toString());
                }).toList();

        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUsers(@PathVariable(name = "id") Long id) {
        User user = userService.findById(id).get();

        UserDto userDto = new UserDto(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.isBanned(),
                user.getContactPerson(),
                user.getPhone(),
                user.getName(),
                user.getAddress(),
                user.getDescText(),
                user.getLogotype(),
                user.getWorkTime(),
                user.getCreatedAt(),
                null, user.getRoles().toString());

        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/users/wait")
    public ResponseEntity<?> getWaitUsers() {
        List<User> users = userService.findAll();

        List<UserDto> userDtos = users.stream().filter((item) -> {
            return item.isBanned() && !item.getUsername().equals("admin");
        }).map((item) -> {
            return new UserDto(
                    item.getId(),
                    item.getUsername(),
                    item.getPassword(),
                    item.getEmail(),
                    item.isBanned(),
                    item.getContactPerson(),
                    item.getPhone(),
                    item.getName(),
                    item.getAddress(),
                    item.getDescText(),
                    item.getLogotype(),
                    item.getWorkTime(),
                    item.getCreatedAt(),
                    null, item.getRoles().toString());
        }).toList();

        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/users/allow")
    public ResponseEntity<?> getAllowUsers() {
        List<User> users = userService.findAll();

        List<UserDto> userDtos = users.stream().filter((item) -> {
            return !item.isBanned() && !item.getUsername().equals("admin");
        }).map((item) -> {
            return new UserDto(
                    item.getId(),
                    item.getUsername(),
                    item.getPassword(),
                    item.getEmail(),
                    item.isBanned(),
                    item.getContactPerson(),
                    item.getPhone(),
                    item.getName(),
                    item.getAddress(),
                    item.getDescText(),
                    item.getLogotype(),
                    item.getWorkTime(),
                    item.getCreatedAt(),
                    null, item.getRoles().toString());
        }).toList();

        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/set/roles")
    public String setRoles() {
        userService.setRoles();
        return "Ok roles!";
    }

    @GetMapping("/set/admin")
    public String setAdmin() {
        userService.createAdmin();
        return "Ok admin!";
    }
}
