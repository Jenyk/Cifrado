package filesystem;

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

    void initialize() throws IOException;

    FileRecord createFile(InputStream content, String path) throws InvalidPathException, PathCollisionException, IOException;

    List<FileRecord> listFiles(String path) throws InvalidPathException, IOException;

    FileRecord getFile(String path) throws InvalidPathException;

    void deleteFile(String path) throws InvalidPathException, IOException;

    void createDirectory (String path) throws InvalidPathException, PathCollisionException, IOException;

    void deleteDirectory(String path) throws InvalidPathException, IOException;

    // TODO: mazání složek?
}
