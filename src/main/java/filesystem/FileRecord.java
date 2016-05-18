package filesystem;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by petrkubat on 18/05/16.
 */
public abstract class FileRecord {
    public abstract String getPath();
    public abstract boolean isDirectory();
    public abstract InputStream getStream() throws IOException;
}
