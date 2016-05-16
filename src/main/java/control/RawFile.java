package control;

import java.io.InputStream;

/**
 * Created by petrkubat on 05/05/16.
 */
public class RawFile {
    private String fileName;
    private InputStream data;

    public RawFile(String fileName, InputStream data) {
        this.fileName = fileName;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getData() {
        return data;
    }
}
