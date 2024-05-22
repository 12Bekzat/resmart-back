package com.app.resmart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.resmart.dto.JwtRequest;
import com.app.resmart.dto.JwtResponse;
import com.app.resmart.dto.RegUserDto;
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
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Incorrect login or password"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(jwtRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegUserDto regUserDto) {
        if (userService.findByUsername(regUserDto.getUsername()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "User is already exist!"), HttpStatus.BAD_REQUEST);
        }
        User user = userService.createNewUser(regUserDto);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/my")
    public String getMy() {
        return "My Info Correct!";
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
