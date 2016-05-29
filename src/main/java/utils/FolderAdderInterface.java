package utils;

import filesystem.exceptions.FileSystemException;
import filesystem.exceptions.InvalidPathException;
import filesystem.exceptions.PathCollisionException;
import security.exceptions.EncryptionException;
import security.exceptions.IntegrityException;

import java.io.IOException;
import java.nio.file.Path;
import java.security.spec.InvalidParameterSpecException;

/**
 * Created by petrkubat on 28/05/16.
 */
public interface FolderAdderInterface {
    void addFolder(Path localPath, String targetPath, String password) throws IOException, PathCollisionException,
            EncryptionException, IntegrityException, FileSystemException, InvalidPathException, InvalidParameterSpecException;

    void removeFolder(Path path) throws InvalidPathException, IntegrityException, InvalidParameterSpecException, FileSystemException, IOException;
}
