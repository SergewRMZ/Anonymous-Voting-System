package com.voting_system.election_service.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import com.voting_system.election_service.domain.CandidatePhotoModel;
import com.voting_system.election_service.exceptions.CandidateLockedException;
import com.voting_system.election_service.exceptions.CandidateNotFoundException;
import com.voting_system.election_service.exceptions.CandidatePhotoNotFoundException;
import com.voting_system.election_service.exceptions.CandidatePhotoTooLargeException;
import com.voting_system.election_service.exceptions.InvalidCandidatePhotoException;
import com.voting_system.election_service.exceptions.UnsupportedCandidatePhotoTypeException;
import com.voting_system.election_service.repository.interfaces.ICandidacyRepository;
import com.voting_system.election_service.repository.interfaces.ICandidatePhotoRepository;
import com.voting_system.election_service.repository.interfaces.ICandidateRepository;

@Service
public class CandidatePhotoService {
    private static final byte[] JPEG_SIGNATURE = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF };
    private static final byte[] PNG_SIGNATURE = { (byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n' };
    private static final byte[] RIFF = "RIFF".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] WEBP = "WEBP".getBytes(StandardCharsets.US_ASCII);

    private final ICandidateRepository candidateRepository;
    private final ICandidacyRepository candidacyRepository;
    private final ICandidatePhotoRepository candidatePhotoRepository;
    private final DataSize maxSize;

    public CandidatePhotoService(
        ICandidateRepository candidateRepository,
        ICandidacyRepository candidacyRepository,
        ICandidatePhotoRepository candidatePhotoRepository,
        @Value("${election.candidate-photo.max-size:2MB}") DataSize maxSize
    ) {
        this.candidateRepository = candidateRepository;
        this.candidacyRepository = candidacyRepository;
        this.candidatePhotoRepository = candidatePhotoRepository;
        this.maxSize = maxSize;
    }

    public void savePhoto(UUID candidateId, MultipartFile file) {
        if (!candidateRepository.existsById(candidateId)) {
            throw new CandidateNotFoundException("Candidate not found with ID: " + candidateId);
        }

        // La boleta no debe cambiar después de publicar la elección.
        if (candidacyRepository.isCandidateInNonDraftElection(candidateId)) {
            throw new CandidateLockedException(
                "The candidate belongs to an election that is no longer in DRAFT, its photo cannot be changed");
        }

        if (file == null || file.isEmpty()) {
            throw new InvalidCandidatePhotoException("The photo file is empty");
        }

        if (file.getSize() > maxSize.toBytes()) {
            throw new CandidatePhotoTooLargeException(
                "The photo must not exceed " + maxSize.toKilobytes() + " KB");
        }

        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException ex) {
            throw new InvalidCandidatePhotoException("The photo file could not be read");
        }

        // El tipo se decide por el contenido, no por la extensión ni por el Content-Type del cliente.
        String contentType = detectContentType(data);
        if (contentType == null) {
            throw new UnsupportedCandidatePhotoTypeException("Only JPEG, PNG and WebP images are allowed");
        }

        candidatePhotoRepository.save(new CandidatePhotoModel(candidateId, contentType, sha256(data), data));
    }

    public CandidatePhotoModel getPhoto(UUID candidateId) {
        return candidatePhotoRepository.findByCandidateId(candidateId)
            .orElseThrow(() -> new CandidatePhotoNotFoundException(
                "The candidate with ID " + candidateId + " has no photo"));
    }

    static String detectContentType(byte[] data) {
        if (startsWith(data, 0, JPEG_SIGNATURE)) {
            return "image/jpeg";
        }
        if (startsWith(data, 0, PNG_SIGNATURE)) {
            return "image/png";
        }
        if (startsWith(data, 0, RIFF) && startsWith(data, 8, WEBP)) {
            return "image/webp";
        }
        return null;
    }

    private static boolean startsWith(byte[] data, int offset, byte[] signature) {
        return data.length >= offset + signature.length
            && Arrays.equals(data, offset, offset + signature.length, signature, 0, signature.length);
    }

    private static String sha256(byte[] data) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(data));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }
}
