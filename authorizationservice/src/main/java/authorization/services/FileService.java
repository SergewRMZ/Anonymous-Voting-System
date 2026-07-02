package authorization.services;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    private static final String KEYS_DIRECTORY = "./keys/";

    public void saveFileToPem(String filename, String description, byte[] keyBytes) {
        try {
            File directory = new File(KEYS_DIRECTORY);
            if(!directory.exists()) directory.mkdirs();

            File file = new File(KEYS_DIRECTORY + filename);
            try (PemWriter pemWriter = new PemWriter(new FileWriter(file))) {
                PemObject pemObject = new PemObject(description, keyBytes);
                pemWriter.writeObject(pemObject);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error crítico de E/S: ", e);
        }
    }

    public byte[] readFileFromPem(String filename) {
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
}
