package com.voting_system.authentication_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.authentication_service.dto.UserLoginRequestDTO;
import com.voting_system.authentication_service.dto.UserLoginResponseDTO;
import com.voting_system.authentication_service.dto.UserRegisterRequestDTO;
import com.voting_system.authentication_service.dto.UserRegisterResponseDTO;
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
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponseDTO> register(@Valid @RequestBody UserRegisterRequestDTO request) {
        userService.registerVoter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            UserRegisterResponseDTO.from("User has been registered correctly"));
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO request) {
        return ResponseEntity.status(HttpStatus.OK).body(
            UserLoginResponseDTO.from(userService.loginUser(request))
        );
    }
}
