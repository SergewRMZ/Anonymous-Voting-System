package com.voting_system.election_service.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.unit.DataSize;

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

class CandidatePhotoServiceTest {
    private static final byte[] PNG = { (byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n', 0, 0 };
    private static final byte[] JPEG = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0 };
    private static final byte[] WEBP = { 'R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'E', 'B', 'P', 0 };

    private ICandidateRepository candidateRepository;
    private ICandidacyRepository candidacyRepository;
    private ICandidatePhotoRepository photoRepository;
    private CandidatePhotoService service;

    private final UUID candidateId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        candidateRepository = mock(ICandidateRepository.class);
        candidacyRepository = mock(ICandidacyRepository.class);
        photoRepository = mock(ICandidatePhotoRepository.class);
        service = new CandidatePhotoService(candidateRepository, candidacyRepository, photoRepository, DataSize.ofBytes(16));

        when(candidateRepository.existsById(candidateId)).thenReturn(true);
        when(candidacyRepository.isCandidateInNonDraftElection(candidateId)).thenReturn(false);
    }

    private static MockMultipartFile file(byte[] content) {
        // El Content-Type del cliente se ignora a propósito.
        return new MockMultipartFile("file", "photo.png", "image/png", content);
    }

    @Test
    void savesPngWithDetectedContentTypeAndHash() {
        service.savePhoto(candidateId, file(PNG));

        ArgumentCaptor<CandidatePhotoModel> captor = ArgumentCaptor.forClass(CandidatePhotoModel.class);
        verify(photoRepository).save(captor.capture());
        assertEquals("image/png", captor.getValue().contentType());
        assertEquals(64, captor.getValue().sha256().length());
        assertEquals(candidateId, captor.getValue().candidateId());
    }

    @Test
    void detectsJpegAndWebp() {
        assertEquals("image/jpeg", CandidatePhotoService.detectContentType(JPEG));
        assertEquals("image/webp", CandidatePhotoService.detectContentType(WEBP));
    }

    @Test
    void rejectsContentThatIsNotAnAllowedImage() {
        byte[] svg = "<svg onload=x>".getBytes();
        assertNull(CandidatePhotoService.detectContentType(svg));
        assertThrows(UnsupportedCandidatePhotoTypeException.class, () -> service.savePhoto(candidateId, file(svg)));
        verify(photoRepository, never()).save(any());
    }

    @Test
    void rejectsFileLargerThanTheLimit() {
        byte[] tooLarge = Arrays.copyOf(PNG, 17);
        assertThrows(CandidatePhotoTooLargeException.class, () -> service.savePhoto(candidateId, file(tooLarge)));
        verify(photoRepository, never()).save(any());
    }

    @Test
    void acceptsFileExactlyAtTheLimit() {
        service.savePhoto(candidateId, file(Arrays.copyOf(PNG, 16)));
        verify(photoRepository).save(any());
    }

    @Test
    void rejectsEmptyFile() {
        assertThrows(InvalidCandidatePhotoException.class, () -> service.savePhoto(candidateId, file(new byte[0])));
    }

    @Test
    void rejectsUnknownCandidate() {
        when(candidateRepository.existsById(candidateId)).thenReturn(false);
        assertThrows(CandidateNotFoundException.class, () -> service.savePhoto(candidateId, file(PNG)));
        verify(photoRepository, never()).save(any());
    }

    @Test
    void rejectsCandidateInElectionOutsideDraft() {
        when(candidacyRepository.isCandidateInNonDraftElection(candidateId)).thenReturn(true);
        assertThrows(CandidateLockedException.class, () -> service.savePhoto(candidateId, file(PNG)));
        verify(photoRepository, never()).save(any());
    }

    @Test
    void getPhotoFailsWhenCandidateHasNone() {
        when(photoRepository.findByCandidateId(candidateId)).thenReturn(Optional.empty());
        assertThrows(CandidatePhotoNotFoundException.class, () -> service.getPhoto(candidateId));
    }
}
