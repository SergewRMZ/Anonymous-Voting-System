package com.voting_system.authentication_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.authentication_service.dto.VoterRegistrationDto;
import com.voting_system.authentication_service.dto.VoterRegistrationPageDto;
import com.voting_system.authentication_service.model.VoterRegistrationStatus;
import com.voting_system.authentication_service.services.VoterRegistrationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth/voters")
@RequiredArgsConstructor
public class VoterRegistrationController {
    private static final int MAX_PAGE_SIZE = 100;

    private final VoterRegistrationService voterRegistrationService;

    @GetMapping("")
    public ResponseEntity<VoterRegistrationPageDto> getRegistrations(
        @RequestParam(required = false) VoterRegistrationStatus status,
        @RequestParam(required = false) String search,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return ResponseEntity.ok(
            voterRegistrationService.findRegistrations(status, search, safePage, safeSize));
    }

    @PatchMapping("/{userId}/approve")
    public ResponseEntity<VoterRegistrationDto> approve(@PathVariable String userId) {
        return ResponseEntity.ok(voterRegistrationService.approve(userId));
    }
}
