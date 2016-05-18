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
import security.exceptions.IntegrityException;
import java.io.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by petrkubat on 11/05/16.
 */
public class EncryptionService implements EncryptionServiceInterface {
    FileSystemServiceInterface fileSystemService;
    EncryptionProviderInterface encryptionProvider;
    IntegrityProviderInterface integrityProvider;

    // TODO: Exceptions
    // TODO: Logging

    public EncryptionService(FileSystemServiceInterface fileSystemService, EncryptionProviderInterface encryptionProvider, IntegrityProviderInterface integrityProvider) {
        this.fileSystemService = fileSystemService;
        this.encryptionProvider = encryptionProvider;
        this.integrityProvider = integrityProvider;
    }

    @Override
    public void addFile(RawFile file, String targetPath, String password) {
        try {
            InputStream encryptedData = encryptionProvider.encryptData(file.getData(), password, targetPath);
            File newFile = fileSystemService.createFile(encryptedData, targetPath);
            integrityProvider.trackNewFile(newFile, password, targetPath);
        } catch (EncryptionException e) {
            e.printStackTrace();
        } catch (PathCollisionException e) {
            e.printStackTrace();
        } catch (InvalidPathException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IntegrityException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void moveFile(String oldPath, String newPath, String password) {
        RawFile oldFile = getFile(oldPath, password);
        addFile(oldFile, newPath, password);
        deleteFile(oldPath, password);
    }

    @Override
    public List<EncryptedFileStatus> listFiles(String path, String password) {
        List<File> fileList = null;
        List<EncryptedFileStatus> statusList = new ArrayList<>();
        try {
            fileList = fileSystemService.listFiles(path);
        } catch (InvalidPathException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (File file: fileList) {
            boolean integrity = false;
            try {
                integrity = integrityProvider.checkFileIntegrity(file, password, path + "/" + file.getName());
            } catch (IntegrityException e) {
                e.printStackTrace();
            } catch (InvalidParameterSpecException e) {
                e.printStackTrace();
            }
            EncryptedFileStatus status = new EncryptedFileStatus(file.getName(), integrity, file.isDirectory());
            statusList.add(status);
        }
        return statusList;
    }

    @Override
    public void deleteFile(String path, String password) {
        try {
            integrityProvider.stopTrackingFile(path);
            fileSystemService.deleteFile(path);
        } catch (InvalidPathException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IntegrityException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }

    }

    @Override
    public RawFile getFile(String path, String password) {
        try {
            File file = fileSystemService.getFile(path);
            boolean fileOk = integrityProvider.checkFileIntegrity(file, password, path);
            if (fileOk) {
                InputStream encryptedData = new FileInputStream(file);
                InputStream decryptedData = encryptionProvider.decryptData(encryptedData, password, path);
                return new RawFile(path, decryptedData);
            } else {
                System.out.printf("File is not integral.");
            }
        } catch (InvalidPathException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        } catch (IntegrityException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }

        // - check integrity
        // - decrypt
        // - return RawFile

        return null;
    }
}
