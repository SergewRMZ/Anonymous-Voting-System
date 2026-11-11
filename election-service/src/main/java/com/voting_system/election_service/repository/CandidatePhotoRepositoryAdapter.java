package com.voting_system.election_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.voting_system.election_service.domain.CandidatePhotoModel;
import com.voting_system.election_service.entity.JpaCandidatePhotoEntity;
import com.voting_system.election_service.repository.interfaces.ICandidatePhotoRepository;
import com.voting_system.election_service.repository.jpa.JpaCandidatePhotoRepository;
import com.voting_system.election_service.repository.jpa.JpaCandidateRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CandidatePhotoRepositoryAdapter implements ICandidatePhotoRepository {
    private final JpaCandidatePhotoRepository jpaCandidatePhotoRepository;
    private final JpaCandidateRepository jpaCandidateRepository;

    // Crea la foto o reemplaza la existente.
    @Override
    @Transactional
    public void save(CandidatePhotoModel photo) {
        JpaCandidatePhotoEntity entity = jpaCandidatePhotoRepository.findById(photo.candidateId())
            .orElseGet(() -> {
                JpaCandidatePhotoEntity created = new JpaCandidatePhotoEntity();
                created.setCandidate(jpaCandidateRepository.getReferenceById(photo.candidateId()));
                return created;
            });

        entity.setContentType(photo.contentType());
        entity.setSizeBytes(photo.data().length);
        entity.setSha256(photo.sha256());
        entity.setData(photo.data());
        jpaCandidatePhotoRepository.save(entity);
    }

    @Override
    @Transactional (readOnly = true)
    public Optional<CandidatePhotoModel> findByCandidateId(UUID candidateId) {
        return jpaCandidatePhotoRepository.findById(candidateId)
            .map(entity -> new CandidatePhotoModel(
                entity.getCandidateId(),
                entity.getContentType(),
                entity.getSha256(),
                entity.getData()
            ));
    }
}
