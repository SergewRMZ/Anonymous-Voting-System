package com.voting_system.election_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.voting_system.election_service.domain.CandidateModel;
import com.voting_system.election_service.domain.CandidatePhotoModel;
import com.voting_system.election_service.dto.CandidateDtoResponse;
import com.voting_system.election_service.dto.CreateCandidateDtoRequest;
import com.voting_system.election_service.services.CandidatePhotoService;
import com.voting_system.election_service.services.CandidateService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping ("/api/election-service/candidates")
@RequiredArgsConstructor
public class CandidateController {
    private final CandidateService candidateService;
    private final CandidatePhotoService candidatePhotoService;

    @PostMapping("")
    public ResponseEntity<CandidateDtoResponse> createCandidate(@Valid @RequestBody CreateCandidateDtoRequest request) {
        CandidateModel model = candidateService.createCandidate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CandidateDtoResponse.fromModel(model));
    }

    @GetMapping("")
    public ResponseEntity<List<CandidateDtoResponse>> getCandidates() {
        return ResponseEntity.ok(candidateService.getCandidates()
            .stream()
            .map(CandidateDtoResponse::fromModel)
            .toList());
    }

    @PutMapping(value = "/{candidateId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadPhoto(
        @PathVariable UUID candidateId,
        @RequestParam("file") MultipartFile file
    ) {
        candidatePhotoService.savePhoto(candidateId, file);
        return ResponseEntity.noContent().build();
    }

    // Público: un <img> no puede enviar el token, y la foto ya es pública en la boleta.
    @GetMapping("/{candidateId}/photo")
    public ResponseEntity<byte[]> getPhoto(
        @PathVariable UUID candidateId,
        @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch
    ) {
        CandidatePhotoModel photo = candidatePhotoService.getPhoto(candidateId);
        String etag = "\"" + photo.sha256() + "\"";

        // no-cache: el navegador guarda la imagen, pero la revalida con el ETag
        // para enterarse si el administrador la reemplazó.
        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                .eTag(etag)
                .cacheControl(CacheControl.noCache())
                .build();
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(photo.contentType()))
            .eTag(etag)
            .cacheControl(CacheControl.noCache())
            .body(photo.data());
    }
}
