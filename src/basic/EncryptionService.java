package basic;

import control.EncryptedFileStatus;
import control.EncryptionServiceInterface;
import control.RawFile;
import filesystem.FileSystemServiceInterface;
import security.EncryptionProviderInterface;
import security.IntegrityProviderInterface;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by petrkubat on 11/05/16.
 */
public class EncryptionService implements EncryptionServiceInterface {
    FileSystemServiceInterface fileSystemService;
    EncryptionProviderInterface encryptionProvider;
    IntegrityProviderInterface integrityProvider;

    @Override
    public void addFile(RawFile file, String targetPath, String password) {
        InputStream encryptedData = encryptionProvider.encryptData(file.getData(), password, targetPath);
        File newFile = fileSystemService.createFile(encryptedData, targetPath);
        integrityProvider.trackNewFile(newFile, password);
    }

    @Override
    public void moveFile(String oldPath, String newPath, String password) {

    }

    @Override
    public List<EncryptedFileStatus> listFiles(String path, String password) {
        List<File> fileList = fileSystemService.listFiles(path);

        // For each file:
        //  - verify integrity
        //  - create EncryptedFileStatus
        // ?? hash filtering ??
        return null;
    }

    @Override
    public void deleteFile(String path, String password) {
        File file = fileSystemService.getFile(path);
        integrityProvider.stopTrackingFile(file);
        fileSystemService.deleteFile(path);
    }

    @Override
    public RawFile getFile(String path, String password) {
        File file = fileSystemService.getFile(path);

        // - check integrity
        // - decrypt
        // - return RawFile

        return null;
    }
}
