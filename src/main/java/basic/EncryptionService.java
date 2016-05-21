package basic;

import control.EncryptedFileStatus;
import control.EncryptionServiceInterface;
import control.RawFile;
import filesystem.FileRecord;
import filesystem.FileSystemServiceInterface;
import filesystem.exceptions.FileSystemException;
import filesystem.exceptions.InvalidPathException;
import filesystem.exceptions.PathCollisionException;
import org.omg.CORBA.DynAnyPackage.Invalid;
import security.exceptions.EncryptionException;
import security.EncryptionProviderInterface;
import security.IntegrityProviderInterface;
import security.exceptions.IntegrityException;
import java.io.*;
import java.nio.file.Path;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by petrkubat on 11/05/16.
 */
public class EncryptionService implements EncryptionServiceInterface {
    private final static Logger log = Logger.getLogger(EncryptionService.class.getName());

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
    public void addFile(RawFile file, String targetPath, String password) throws EncryptionException,
            FileSystemException, IntegrityException, PathCollisionException, InvalidPathException, InvalidParameterSpecException {
        try {
            InputStream encryptedData = encryptionProvider.encryptData(file.getData(), password, targetPath);
            FileRecord newFile = fileSystemService.createFile(encryptedData, targetPath);
            integrityProvider.trackNewFile(newFile, password);
        } catch (EncryptionException e) {
            log.log(Level.SEVERE, "Error while encrypting the data", e);
            throw e;
        } catch (FileSystemException e) {
            log.log(Level.SEVERE, "File system error while adding the file", e);
            throw e;
        } catch (IntegrityException e) {
            log.log(Level.SEVERE, "Error while trying to track the file integrity", e);
            throw e;
        } catch (PathCollisionException e) {
            log.log(Level.SEVERE, "Trying to add file to a colliding path: " + targetPath, e);
            throw e;
        } catch (InvalidPathException e) {
            log.log(Level.SEVERE, "Trying to add file to an invalid path: " + targetPath, e);
            throw e;
        } catch (InvalidParameterSpecException e) {
            log.log(Level.SEVERE, "Invalid arguments", e);
            throw e;
        }

    }

    @Override
    public void moveFile(String oldPath, String newPath, String password) throws EncryptionException,
            FileSystemException, IntegrityException, PathCollisionException, InvalidPathException, InvalidParameterSpecException {
        RawFile oldFile = getFile(oldPath, password);
        addFile(oldFile, newPath, password);
        deleteFile(oldPath, password);
    }

    @Override
    public List<EncryptedFileStatus> listFiles(String path, String password) throws InvalidPathException,
            FileSystemException, IntegrityException, InvalidParameterSpecException {
        List<FileRecord> fileList = null;

        try {
            fileList = fileSystemService.listFiles(path);
        } catch (InvalidPathException e) {
            log.log(Level.SEVERE, "Trying to list files from an invalid path: " + path, e);
            throw e;
        } catch (FileSystemException e) {
            log.log(Level.SEVERE, "File system error while listing files", e);
            throw e;
        }

        List<EncryptedFileStatus> result = new ArrayList<EncryptedFileStatus>();

        for (FileRecord fileRecord : fileList) {
            try {
                boolean integrity = integrityProvider.checkFileIntegrity(fileRecord, password);
                result.add(new EncryptedFileStatus(fileRecord.getPath(), integrity, fileRecord.isDirectory()));
            } catch (IntegrityException e) {
                log.log(Level.SEVERE, "Error trying to check the file integrity for: " + fileRecord.getPath(), e);
                throw e;
            } catch (InvalidParameterSpecException e) {
                log.log(Level.SEVERE, "Invalid arguments", e);
                throw e;
            }
        }

        return result;
    }

    @Override
    public void deleteFile(String path, String password) throws InvalidPathException,
            FileSystemException, IntegrityException, InvalidParameterSpecException {
        try {
            integrityProvider.stopTrackingFile(path);
            fileSystemService.deleteFile(path);
        } catch (InvalidPathException e) {
            log.log(Level.SEVERE, "Invalid arguments", e);
            throw e;
        } catch (FileSystemException e) {
            log.log(Level.SEVERE, "File system error while deleting file", e);
            throw e;
        } catch (IntegrityException e) {
            log.log(Level.SEVERE, "Error while trying to stop tracking file integrity", e);
            throw e;
        } catch (InvalidParameterSpecException e) {
            log.log(Level.SEVERE, "Invalid arguments", e);
            throw e;
        }

    }

    @Override
    public RawFile getFile(String path, String password) throws EncryptionException,
            FileSystemException, InvalidPathException, InvalidParameterSpecException {
        try {
            FileRecord file = fileSystemService.getFile(path);
            try (InputStream encryptedData = file.getStream()) {
                InputStream decryptedData = encryptionProvider.decryptData(encryptedData, password, path);
                return new RawFile(path, decryptedData);
            }
        } catch (InvalidPathException e) {
            log.log(Level.SEVERE, "Trying to retrieve a file from an invalid path: " + path, e);
            throw e;
        } catch (EncryptionException e) {
            log.log(Level.SEVERE, "Error while decrypting the data", e);
            throw e;
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error while accessing the file", e);
            throw new FileSystemException(e);
        } catch (InvalidParameterSpecException e) {
            log.log(Level.SEVERE, "Invalid arguments", e);
            throw e;
        }
    }

    @Override
    public void createDirectory(String path) throws InvalidPathException, PathCollisionException, FileSystemException {
        try {
            fileSystemService.createDirectory(path);
        } catch (InvalidPathException ex) {
            log.log(Level.SEVERE, "Trying to create a directory with an invalid path: " + path, ex);
            throw ex;
        } catch (PathCollisionException ex) {
            log.log(Level.SEVERE, "Trying to create a directory with a colliding path: " + path, ex);
            throw ex;
        } catch (FileSystemException ex) {
            log.log(Level.SEVERE, "Error while creating the directory", ex);
            throw ex;
        }
    }

    @Override
    public void deleteDirectory(String path) throws IntegrityException,
            FileSystemException, InvalidPathException, InvalidParameterSpecException {
        try {
            // We need to stop integrity tracking for all the files

            for (FileRecord file: fileSystemService.listFilesRecursive(path)) {
                integrityProvider.stopTrackingFile(file.getPath());
            }

            fileSystemService.deleteDirectory(path);

        } catch (IntegrityException e) {
            log.log(Level.SEVERE, "Error while trying to stop integrity tracking", e);
            throw e;
        } catch (FileSystemException e) {
            log.log(Level.SEVERE, "Error while trying to delete the directory", e);
            throw e;
        } catch (InvalidPathException e) {
            log.log(Level.SEVERE, "Trying to delete a directory with invalid path: " + path, e);
            throw e;
        } catch (InvalidParameterSpecException e) {
            log.log(Level.SEVERE, "Invalid arguments", e);
            throw e;
        }
    }
}
