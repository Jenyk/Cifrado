package control;

/**
 * Created by petrkubat on 05/05/16.
 */
public class EncryptedFileStatus {
    private String fileName;
    private boolean isIntegral;
    private boolean isFolder;

    public EncryptedFileStatus(String fileName, boolean isIntegral, boolean isFolder) {
        this.fileName = fileName;
        this.isIntegral = isIntegral;
        this.isFolder = isFolder;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isIntegral() {
        return isIntegral;
    }

    public boolean isFolder() {
        return isFolder;
    }
}
