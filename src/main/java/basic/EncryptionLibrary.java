package basic;

import control.EncryptionServiceInterface;
import filesystem.FileSystemServiceInterface;
import filesystem.local.LocalFileStorage;
import filesystem.local.deletion.SecureFileDeleter;
import filesystem.local.deletion.SimpleFileDeleter;
import security.AesEncryptionProvider;
import security.HmacIntegrityProvider;

import java.io.IOException;

/**
 * Created by petrkubat on 19/05/16.
 */
public class EncryptionLibrary {
    public static EncryptionServiceInterface getEncryptionService(String dataRootDirectory, String integrityRootDirectory) {
        FileSystemServiceInterface dataStorage = new LocalFileStorage(dataRootDirectory, new SecureFileDeleter());
        FileSystemServiceInterface integrityStorage = new LocalFileStorage(integrityRootDirectory, new SimpleFileDeleter());

        try {
            dataStorage.initialize();
            integrityStorage.initialize();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return new EncryptionService(dataStorage, new AesEncryptionProvider(), new HmacIntegrityProvider(integrityStorage));
    }
}
