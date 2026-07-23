package authorization.repository;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.util.UUID;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

public class KeyRepositoryPemFileImpl implements KeyRepository {
    private final String KEYS_DIRECTORY = "./keys/";
    private final String PRIVATE_KEY_EXTENSION = "_private.pem";
    private final String PUBLIC_KEY_EXTENSION = "_public.pem";

    private void saveKeyToPem(String filename, String description, byte[] keyBytes) {
        try {
            File directory = new File(KEYS_DIRECTORY);
            if(!directory.exists()) directory.mkdirs();

            File file = new File(KEYS_DIRECTORY + filename);
            try (PemWriter pemWriter = new PemWriter(new FileWriter(file))) {
                PemObject pemObject = new PemObject(description, keyBytes);
                pemWriter.writeObject(pemObject);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo PEM de la clave: ", e);
        }
    }

    private byte[] readKeyFromPem(String filename) {
        File file = new File(KEYS_DIRECTORY + filename);
        if (!file.exists()) {
            throw new RuntimeException("Archivo no encontrado: " + filename);
        }

        try (PemReader pemReader = new PemReader(new FileReader(file))) {
            return pemReader.readPemObject().getContent();
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo PEM: ", e);
        }
    }

    @Override
    public void saveKeyPair(UUID electionId, KeyPair keyPair) {
        String privateKeyFile = electionId + PRIVATE_KEY_EXTENSION;
        String publicKeyFile = electionId + PUBLIC_KEY_EXTENSION;
        saveKeyToPem(privateKeyFile, "PRIVATE KEY", keyPair.getPrivate().getEncoded());
        saveKeyToPem(publicKeyFile, "PUBLIC KEY", keyPair.getPublic().getEncoded());
    }

    @Override
    public byte[] readPublicKey(UUID electionId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readPublicKey'");
    }

    @Override
    public byte[] readPrivateKey(UUID electionId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readPrivateKey'");
    }
}
