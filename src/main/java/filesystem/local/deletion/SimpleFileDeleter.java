package filesystem.local.deletion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by petrkubat on 18/05/16.
 */
public class SimpleFileDeleter implements FileDeleterInterface {

    @Override
    public void deleteFile(Path filePath) throws IOException {
        Files.delete(filePath);
    }
}
