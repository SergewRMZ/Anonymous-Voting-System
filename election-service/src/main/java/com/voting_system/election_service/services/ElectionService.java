package com.voting_system.election_service.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.voting_system.election_service.domain.ElectionModel;
import com.voting_system.election_service.dto.CreateElectionDtoRequest;
import com.voting_system.election_service.repository.interfaces.IElectionRepository;
import com.voting_system.election_service.services.interfaces.IElectionService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ElectionService implements IElectionService {
    private final IElectionRepository electionRepository;

    @Override 
    public ElectionModel createElection(CreateElectionDtoRequest createElectionDtoRequest) {
        ElectionModel electionModel = ElectionModel.builder()
            .name(createElectionDtoRequest.name())
            .description(createElectionDtoRequest.description())
            .startDate(createElectionDtoRequest.startDate())
            .endDate(createElectionDtoRequest.endDate()).build();

        
        electionModel.createElection();
        return electionRepository.save(electionModel);
    }

    @Override 
    public List<ElectionModel> getElections() {
        return electionRepository.getElections();
    }
}
