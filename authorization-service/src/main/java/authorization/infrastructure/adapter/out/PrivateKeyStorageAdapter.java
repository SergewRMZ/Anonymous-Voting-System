package authorization.infrastructure.adapter.out;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import authorization.application.port.out.PrivateKeyStoragePort;

@Component
public class PrivateKeyStorageAdapter implements PrivateKeyStoragePort {
    private final Path keysDirectory;
    private static final String PRIVATE_KEY_EXTENSION = "_private.pem";

    /**
     * Constructor for KeyStorageAdapter.
     * @param keysDir The directory where cryptographic keys will be stored.
     */
    public PrivateKeyStorageAdapter(@Value("${app.keys.directory:./keys}") String keysDir) {
        this.keysDirectory = Paths.get(keysDir).toAbsolutePath().normalize();
    }

    /**
     * Resolves the given filename against the keys directory and validates that the resolved path is within the keys directory.
     * @param filename The filename to resolve.
     * @return The resolved and validated path.
     */
    private Path resolveAndValidatePath(String filename) { 
        Path resolved = this.keysDirectory.resolve(filename).normalize();
        if(!resolved.startsWith(keysDirectory)) {
            throw new RuntimeException("Acceso no autorizado fuera del directorio");
        }
        return resolved;
    }

    private void saveKeyToPem(String filename, String description, byte[] keyBytes) {
        try {
            if (!Files.exists(keysDirectory)) {
                Files.createDirectories(keysDirectory);
            }

            Path resolvedPath = resolveAndValidatePath(filename);

            try (PemWriter pemWriter = new PemWriter(Files.newBufferedWriter(resolvedPath))) {
                PemObject pemObject = new PemObject(description, keyBytes);
                pemWriter.writeObject(pemObject);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al almacenar una clave criptográfica en formato PEM");
        }
    }

    private byte[] readKeyFromPem(String filename) {
        Path resolvedPath = resolveAndValidatePath(filename);
        if (!Files.exists(resolvedPath)) {
            throw new RuntimeException("Archivo no encontrado: " + filename);
        }

        try (PemReader pemReader = new PemReader(Files.newBufferedReader(resolvedPath))) {
            PemObject pemObject = pemReader.readPemObject();
            if(pemObject == null) {
                throw new RuntimeException("The PEM file is corrupted or has an invalid format");
            }
            return pemObject.getContent();
        }   catch(IOException e) {
            throw new RuntimeException("Failed to read the PEM file", e);
        }
    }

    @Override
    public void savePrivateKey(UUID authorizationKeysId, byte[] privateKeyBytes) {
        saveKeyToPem(
            authorizationKeysId + PRIVATE_KEY_EXTENSION, 
            "PRIVATE KEY", 
            privateKeyBytes
        );
    }

    @Override
    public byte[] readPrivateKey(UUID authorizationKeysId) {
        return readKeyFromPem(authorizationKeysId + PRIVATE_KEY_EXTENSION);
    }
}
