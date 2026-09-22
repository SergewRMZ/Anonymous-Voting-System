package com.voting_system.election_service.domain;

import java.time.Instant;
import java.util.UUID;

import com.voting_system.election_service.exceptions.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class ElectionModel {
    private UUID id;
    private String name;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private ElectionStatus status; 

    public void createElection() {
        if(!this.startDate.isBefore(this.endDate)) {
            throw new InvalidElectionDateException("Start date must be before end date");
        }

        this.status = ElectionStatus.CREATED;
    }

    public void validateElection() {
        if(this.status == ElectionStatus.CREATED) {
            this.status = ElectionStatus.VERIFIED;
            return;
        }
        throw new InvalidElectionStateException("Only elections with CREATED status can be verified");
    }
}
