package filesystem.local;

import filesystem.FileRecord;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by petrkubat on 18/05/16.
 */
public class LocalFileRecord extends FileRecord {
    private String publicPath;
    private Path fileLocation;

    public LocalFileRecord(String publicPath, Path fileLocation) {
        this.publicPath = publicPath;
        this.fileLocation = fileLocation;
    }

    @Override
    public String getPath() {
        return publicPath;
    }

    @Override
    public boolean isDirectory() {
        return Files.isDirectory(fileLocation);
    }

    @Override
    public InputStream getStream() throws IOException {
        return Files.newInputStream(fileLocation);
    }
}
