package com.voting_system.bulletin_board.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class VoteModel {
    private UUID id;
    private UUID electionId;
    private EncryptedVote encryptedVote;
    private String randomizer;
    private String digest;
    private String signature;
}
