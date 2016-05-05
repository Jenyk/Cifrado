package control;

import java.util.List;

/**
 * Created by petrkubat on 05/05/16.
 */
public interface EncryptionServiceInterface {
    public EncryptedFileStatus addFile(RawFile file, String path, String password);
    public EncryptedFileStatus moveFile(String oldPath, String newPath, String password);
    public List<EncryptedFileStatus> listFiles(String path, String password);
    public boolean deleteFile(String path, String password);
    public RawFile getFile(String path, String password);
}
