package filesystem;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by petrkubat on 05/05/16.
 */
public interface FileSystemServiceInterface {
    File createFile(InputStream content, String path);
    List<File> listFiles(String path);
    File getFile(String path);
    void moveFile(String oldPath, String newPath);
    void deleteFile(String path);
}
