package com.voting_system.election_service.repository.jpa;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.voting_system.election_service.entity.JpaCandidatePhotoEntity;

public interface JpaCandidatePhotoRepository extends JpaRepository<JpaCandidatePhotoEntity, UUID> {
    // Solo los identificadores, para no cargar los bytes de las imágenes.
    @Query ("select p.candidateId from JpaCandidatePhotoEntity p where p.candidateId in :candidateIds")
    List<UUID> findCandidateIdsWithPhoto(@Param("candidateIds") Collection<UUID> candidateIds);

    @Query ("select p.candidateId from JpaCandidatePhotoEntity p")
    List<UUID> findAllCandidateIdsWithPhoto();
}
