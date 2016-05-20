package control;

import java.util.List;

/**
 * Created by petrkubat on 05/05/16.
 */
public interface EncryptionServiceInterface {
    // Exceptions coming soon!!!!
    void addFile(RawFile file, String targetPath, String password);

    void moveFile(String oldPath, String newPath, String password);

    List<EncryptedFileStatus> listFiles(String path, String password);

    void deleteFile(String path, String password);

    RawFile getFile(String path, String password);

    void createDirectory(String path);

    void deleteDirectory(String path);
}
