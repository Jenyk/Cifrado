package filesystem;

import filesystem.exceptions.FileSystemException;
import filesystem.exceptions.InvalidPathException;
import filesystem.exceptions.PathCollisionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by petrkubat on 05/05/16.
 */
public interface FileSystemServiceInterface {

    void initialize() throws FileSystemException;

    FileRecord createFile(InputStream content, String path) throws InvalidPathException, PathCollisionException, FileSystemException;

    List<FileRecord> listFiles(String path) throws InvalidPathException, FileSystemException;

    List<FileRecord> listFilesRecursive(String path) throws InvalidPathException, FileSystemException;

    FileRecord getFile(String path) throws InvalidPathException;

    void deleteFile(String path) throws InvalidPathException, FileSystemException;

    void createDirectory (String path) throws InvalidPathException, PathCollisionException, FileSystemException;

    void deleteDirectory(String path) throws InvalidPathException, FileSystemException;
}
