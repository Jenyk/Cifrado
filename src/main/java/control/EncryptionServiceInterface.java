package control;

import filesystem.exceptions.FileSystemException;
import filesystem.exceptions.InvalidPathException;
import filesystem.exceptions.PathCollisionException;
import security.exceptions.EncryptionException;
import security.exceptions.IntegrityException;

import java.security.spec.InvalidParameterSpecException;
import java.util.List;

/**
 * Created by petrkubat on 05/05/16.
 */
public interface EncryptionServiceInterface {
    void addFile(RawFile file, String targetPath, String password) throws EncryptionException,
            FileSystemException, IntegrityException, PathCollisionException, InvalidPathException, InvalidParameterSpecException;

    void moveFile(String oldPath, String newPath, String password) throws EncryptionException,
            FileSystemException, IntegrityException, PathCollisionException, InvalidPathException, InvalidParameterSpecException;

    List<EncryptedFileStatus> listFiles(String path, String password) throws InvalidPathException,
            FileSystemException, IntegrityException, InvalidParameterSpecException;

    void deleteFile(String path, String password) throws InvalidPathException,
            FileSystemException, IntegrityException, InvalidParameterSpecException;

    RawFile getFile(String path, String password) throws EncryptionException,
            FileSystemException, InvalidPathException, InvalidParameterSpecException;

    void createDirectory(String path) throws InvalidPathException, PathCollisionException, FileSystemException;

    void deleteDirectory(String path) throws IntegrityException,
            FileSystemException, InvalidPathException, InvalidParameterSpecException;
}
