package control;

import java.util.List;

/**
 * Created by petrkubat on 05/05/16.
 */
public interface EncryptionServiceInterface {
    EncryptedFileStatus addFile(RawFile file, String path, String password);
    EncryptedFileStatus moveFile(String oldPath, String newPath, String password);
    List<EncryptedFileStatus> listFiles(String path, String password);
    boolean deleteFile(String path, String password);
    RawFile getFile(String path, String password);
}
