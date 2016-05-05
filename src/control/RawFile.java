package control;

/**
 * Created by petrkubat on 05/05/16.
 */
public class RawFile {
    private String fileName;
    private byte[] data;

    public RawFile(String fileName, byte[] data) {
        this.fileName = fileName;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }
}
