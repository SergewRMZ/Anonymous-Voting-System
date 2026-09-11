package com.voting_system.authentication_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.authentication_service.dto.UserRegisterRequestDTO;
import com.voting_system.authentication_service.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping ("/auth")
@RequiredArgsConstructor 
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> postMethodName(@Valid @RequestBody UserRegisterRequestDTO request) {
        String userId = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }
}
