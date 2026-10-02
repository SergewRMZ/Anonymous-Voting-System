package com.voting_system.election_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.dto.CreateElectionDtoRequest;
import com.voting_system.election_service.dto.ElectionDetailsDtoResponse;
import com.voting_system.election_service.dto.ElectionDtoResponse;
import com.voting_system.election_service.services.interfaces.IElectionService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.oauth2.jwt.Jwt;


@RequestMapping("/api/election-service/elections")
@RestController 
@RequiredArgsConstructor 
public class ElectionController {
    private final IElectionService electionService;

    @PostMapping("")
    public ResponseEntity<ElectionDtoResponse> createElection(@RequestBody CreateElectionDtoRequest createElectionDtoRequest) {        
        ElectionModel electionModel = electionService.createElection(createElectionDtoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ElectionDtoResponse.from(electionModel)
        );
    }   

    @PatchMapping ("/{electionId}/publish")
    public ResponseEntity<ElectionDtoResponse> publicElections(@PathVariable UUID electionId) {
        ElectionModel electionModel = electionService.publishElection(electionId);
        return ResponseEntity.ok(ElectionDtoResponse.from(electionModel));
    }

    @PatchMapping ("/{electionId}/validate")
    public ResponseEntity<ElectionDtoResponse> validateElection(@PathVariable UUID electionId) {
        ElectionModel electionModel = electionService.validateElection(electionId);
        return ResponseEntity.ok(ElectionDtoResponse.from(electionModel));
    }

    @PatchMapping ("/{electionId}/activate")
    public ResponseEntity<ElectionDtoResponse> activateElection(@PathVariable UUID electionId) {
        ElectionModel electionModel = electionService.activateElection(electionId);
        return ResponseEntity.ok(ElectionDtoResponse.from(electionModel));
    }


    @GetMapping("")
    public ResponseEntity<List<ElectionDtoResponse>> getElections(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        List<String> roles = List.of();
        if(realmAccess != null && realmAccess.containsKey("roles")) {
            roles = (List<String>) realmAccess.get("roles");
        }

        List<ElectionModel> elections = electionService.getElections(roles);
        return ResponseEntity.ok(elections
            .stream()
            .map(ElectionDtoResponse::from)
            .toList());
    }


    @GetMapping("/{electionId}")
    public ResponseEntity<ElectionDetailsDtoResponse> getElectionDetails(
        @PathVariable UUID electionId,
        @AuthenticationPrincipal Jwt jwt
    ) {

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        List<String> roles = List.of();

        if(realmAccess != null && realmAccess.containsKey("roles")) {
            roles = (List<String>) realmAccess.get("roles");
        }

        return ResponseEntity.ok(electionService
            .getElectionDetails(electionId, roles)
        );
    }
}
