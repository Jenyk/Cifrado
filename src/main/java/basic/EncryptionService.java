package basic;

import control.EncryptedFileStatus;
import control.EncryptionServiceInterface;
import control.RawFile;
import filesystem.FileRecord;
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
import java.util.stream.Collectors;

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
            FileRecord newFile = fileSystemService.createFile(encryptedData, targetPath);
            integrityProvider.trackNewFile(newFile, password);
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
        List<FileRecord> fileList = null;
        List<EncryptedFileStatus> statusList = new ArrayList<>();
        try {
            fileList = fileSystemService.listFiles(path);
        } catch (InvalidPathException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileList.stream().map(fileRecord -> {
            boolean integrity = false;
            try {
                integrity = integrityProvider.checkFileIntegrity(fileRecord, password);
                return new EncryptedFileStatus(fileRecord.getPath(), integrity, fileRecord.isDirectory());
            } catch (IntegrityException e) {
                e.printStackTrace();
            } catch (InvalidParameterSpecException e) {
                e.printStackTrace();
            }
            // TODO: Wrap exception?
            return null;
        }).collect(Collectors.toList());
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
            FileRecord file = fileSystemService.getFile(path);
            boolean fileOk = integrityProvider.checkFileIntegrity(file, password);
            if (fileOk) {
                try (InputStream encryptedData = file.getStream()) {
                    InputStream decryptedData = encryptionProvider.decryptData(encryptedData, password, path);
                    return new RawFile(path, decryptedData);
                }
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
