package com.voting_system.tally_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.voting_system.tally_service.entity.JpaElectionResultEntity;
import com.voting_system.tally_service.exception.ElectionResultsNotFoundException;
import com.voting_system.tally_service.mappers.ElectionResultMapper;
import com.voting_system.tally_service.model.ElectionResultModel;

import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class ElectionResultRepositoryAdapter implements ElectionResultRepositoryPort {
    private final JpaElectionResultRepository jpaElectionResultRepository;
    private final ElectionResultMapper electionResultMapper;

    @Override 
    public List<ElectionResultModel> saveAll(List<ElectionResultModel> resultList) {
        return jpaElectionResultRepository.saveAll(
            resultList.stream()
                .map(electionResultMapper::toEntity)
                .toList()
        )
            .stream()
            .map(electionResultMapper::toModel)
            .toList();
    }

    @Override
    public List<ElectionResultModel> findAllByElectionId(UUID electionId) {
        List<JpaElectionResultEntity> results = jpaElectionResultRepository.findAllByElectionId(electionId)
            .orElseThrow(() -> new ElectionResultsNotFoundException(electionId));
    
        return results
            .stream()
            .map(electionResultMapper::toModel)
            .toList();
    }
    
}
