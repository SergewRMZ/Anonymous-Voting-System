package com.voting_system.election_service.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.voting_system.election_service.domain.CandidateModel;
import com.voting_system.election_service.entity.JpaCandidateEntity;
import com.voting_system.election_service.mappers.CandidateMapper;
import com.voting_system.election_service.repository.interfaces.ICandidateRepository;
import com.voting_system.election_service.repository.jpa.JpaCandidatePhotoRepository;
import com.voting_system.election_service.repository.jpa.JpaCandidateRepository;

import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class CandidateRepositoryAdapter implements ICandidateRepository {
    private final JpaCandidateRepository jpaCandidateRepository;
    private final JpaCandidatePhotoRepository jpaCandidatePhotoRepository;
    private final CandidateMapper candidateMapper;

    @Override
    public CandidateModel save(CandidateModel model) {
        JpaCandidateEntity entity = jpaCandidateRepository.save(candidateMapper.toEntity(model));
        return candidateMapper.toModel(entity);
    }

    @Override
    public List<CandidateModel> getCandidates() {
        Set<UUID> withPhoto = new HashSet<>(jpaCandidatePhotoRepository.findAllCandidateIdsWithPhoto());
        return jpaCandidateRepository.findAll()
            .stream()
            .map(entity -> toModel(entity, withPhoto))
            .toList();
    }

    @Override 
    public List<CandidateModel> findAllByIds(List<UUID> candidateIds) {
        Set<UUID> withPhoto = new HashSet<>(jpaCandidatePhotoRepository.findCandidateIdsWithPhoto(candidateIds));
        return jpaCandidateRepository.findAllById(candidateIds)
            .stream()
            .map(entity -> toModel(entity, withPhoto))
            .toList();
    }

    @Override
    public boolean existsById(UUID candidateId) {
        return jpaCandidateRepository.existsById(candidateId);
    }

    private CandidateModel toModel(JpaCandidateEntity entity, Set<UUID> candidateIdsWithPhoto) {
        CandidateModel model = candidateMapper.toModel(entity);
        model.setHasPhoto(candidateIdsWithPhoto.contains(entity.getId()));
        return model;
    }
}
