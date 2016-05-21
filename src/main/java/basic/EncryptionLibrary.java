package basic;

import control.EncryptionServiceInterface;
import filesystem.FileSystemServiceInterface;
import filesystem.exceptions.FileSystemException;
import filesystem.local.LocalFileStorage;
import filesystem.local.deletion.SecureFileDeleter;
import filesystem.local.deletion.SimpleFileDeleter;
import security.AesEncryptionProvider;
import security.HmacIntegrityProvider;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by petrkubat on 19/05/16.
 */
public class EncryptionLibrary {
    private final static Logger log = Logger.getLogger(EncryptionLibrary.class.getName());

    public static EncryptionServiceInterface getEncryptionService(String dataRootDirectory, String integrityRootDirectory) throws FileSystemException {
        FileSystemServiceInterface dataStorage = new LocalFileStorage(dataRootDirectory, new SecureFileDeleter());
        FileSystemServiceInterface integrityStorage = new LocalFileStorage(integrityRootDirectory, new SimpleFileDeleter());

        try {
            dataStorage.initialize();
            integrityStorage.initialize();
        } catch (FileSystemException ex) {
            log.log(Level.SEVERE, "Error while initializing the storages for EncryptionService", ex);
            throw ex;
        }

        return new EncryptionService(dataStorage, new AesEncryptionProvider(), new HmacIntegrityProvider(integrityStorage));
    }
}
