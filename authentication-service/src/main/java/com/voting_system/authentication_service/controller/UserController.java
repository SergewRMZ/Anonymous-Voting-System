package com.voting_system.authentication_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.voting_system.authentication_service.dto.UserRegisterRequestDTO;
import com.voting_system.authentication_service.dto.UserRegisterResponseDTO;
import com.voting_system.authentication_service.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@RestController 
@RequestMapping ("/users")
@RequiredArgsConstructor 
public class UserController {
    private final UserService userService;
    
    @PostMapping("/admin")
    public ResponseEntity<UserRegisterResponseDTO> createAdmin(@Valid @RequestBody  UserRegisterRequestDTO request) {
        userService.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            UserRegisterResponseDTO.from("Admin user has been registered correctly")
        );
    }

    @PatchMapping ("/voter/{userId}/status")
    public ResponseEntity<?> updateVoterStatus(@Valid 
        @PathVariable String userId,
        @RequestParam boolean enabled
    ) {
        userService.updateVoterStatus(userId, enabled);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
