package filesystem.local.deletion;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by petrkubat on 18/05/16.
 */
public interface FileDeleterInterface {
    void deleteFile(Path filePath) throws IOException;
}
