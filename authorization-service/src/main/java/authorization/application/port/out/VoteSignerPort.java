package authorization.application.port.out;

public interface VoteSignerPort {
    public byte[] signVote(byte[] votePayload, byte[] privateKey);
}
