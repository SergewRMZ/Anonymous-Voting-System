package authorization.application.port.out;
public interface SignerPort {
    public String sign(byte[] privateKeyBytes, byte[] message);
}
