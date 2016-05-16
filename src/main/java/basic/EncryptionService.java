package basic;

import control.EncryptedFileStatus;
import control.EncryptionServiceInterface;
import control.RawFile;
import filesystem.FileSystemServiceInterface;
import filesystem.exceptions.InvalidPathException;
import filesystem.exceptions.PathCollisionException;
import security.exceptions.EncryptionException;
import security.EncryptionProviderInterface;
import security.IntegrityProviderInterface;

import java.io.File;
import java.io.IOException;
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
        try {
            InputStream encryptedData = encryptionProvider.encryptData(file.getData(), password, targetPath);
            File newFile = fileSystemService.createFile(encryptedData, targetPath);
            integrityProvider.trackNewFile(newFile, password);
        } catch (EncryptionException e) {
            e.printStackTrace();
        } catch (PathCollisionException e) {
            e.printStackTrace();
        } catch (InvalidPathException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void moveFile(String oldPath, String newPath, String password) {

    }

    @Override
    public List<EncryptedFileStatus> listFiles(String path, String password) {
        try {
            List<File> fileList = fileSystemService.listFiles(path);
        } catch (InvalidPathException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // For each file:
        //  - verify integrity
        //  - create EncryptedFileStatus
        // ?? hash filtering ??
        return null;
    }

    @Override
    public void deleteFile(String path, String password) {
        try {
            File file = fileSystemService.getFile(path);
            integrityProvider.stopTrackingFile(file);
            fileSystemService.deleteFile(path);
        } catch (InvalidPathException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public RawFile getFile(String path, String password) {
        try {
            File file = fileSystemService.getFile(path);
        } catch (InvalidPathException e) {
            e.printStackTrace();
        }

        // - check integrity
        // - decrypt
        // - return RawFile

        return null;
    }
}
