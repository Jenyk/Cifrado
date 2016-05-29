package control;

/**
 * Created by petrkubat on 05/05/16.
 */
public class EncryptedFileStatus {
    private String path;
    private boolean isIntegral;
    private boolean isFolder;

    public EncryptedFileStatus(String path, boolean isIntegral, boolean isFolder) {
        this.path = path;
        this.isIntegral = isIntegral;
        this.isFolder = isFolder;
    }

    public String getPath() {
        return path;
    }

    public boolean isIntegral() {
        return isIntegral;
    }

    public boolean isFolder() {
        return isFolder;
    }
}
