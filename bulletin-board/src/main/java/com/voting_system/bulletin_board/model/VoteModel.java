package com.voting_system.bulletin_board.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class VoteModel {
    private UUID id;
    private UUID electionId;
    private EncryptedVote vote;
    private String randomizer;
    private String digest;
    private String signature;
}
