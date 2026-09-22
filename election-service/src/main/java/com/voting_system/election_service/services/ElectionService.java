package com.voting_system.election_service.services;

import org.springframework.stereotype.Service;

import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.dto.CreateElectionDtoRequest;
import com.voting_system.election_service.entity.JpaElectionEntity;
import com.voting_system.election_service.mappers.ElectionMapper;
import com.voting_system.election_service.repository.IElectionRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ElectionService implements IElectionService {
    private final IElectionRepository electionRepository;
    private final ElectionMapper electionMapper;

    @Override 
    public ElectionModel createElection(CreateElectionDtoRequest createElectionDtoRequest) {
        ElectionModel electionModel = ElectionModel.builder()
            .name(createElectionDtoRequest.name())
            .description(createElectionDtoRequest.description())
            .startDate(createElectionDtoRequest.startDate())
            .endDate(createElectionDtoRequest.endDate()).build();

        electionModel.createElection();
        JpaElectionEntity jpaElectionEntity = electionRepository.save(electionMapper.toEntity(electionModel));
        return electionMapper.toModel(jpaElectionEntity);
    }
}
