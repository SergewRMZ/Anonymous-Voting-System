package com.voting_system.authentication_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.authentication_service.dto.UserRegisterRequestDTO;
import com.voting_system.authentication_service.dto.UserRegisterResponseDTO;
import com.voting_system.authentication_service.model.UserRole;
import com.voting_system.authentication_service.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController 
@RequestMapping ("/api/auth")
@RequiredArgsConstructor 
public class AuthController {
    private final UserService userService;

    @PostMapping("/admin")
    public ResponseEntity<UserRegisterResponseDTO> createAdmin(@Valid @RequestBody  UserRegisterRequestDTO request) {
        String userId = userService.registerUser(request, UserRole.ROLE_ADMIN, true);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            UserRegisterResponseDTO.from ("Admin user has been registered correctly", userId)
        );
    }

    @PostMapping("/voter")
    public ResponseEntity<UserRegisterResponseDTO> register(@Valid @RequestBody UserRegisterRequestDTO request) {
        String userId = userService.registerUser(request, UserRole.ROLE_VOTER, false);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            UserRegisterResponseDTO.from("User has been registered correctly", userId));
    }

    @PatchMapping ("/voters/{userId}/status")
    public ResponseEntity<?> updateVoterStatus(@PathVariable String userId, @RequestParam boolean enabled) {
        userService.updateVoterStatus(userId, enabled);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
