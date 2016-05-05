package control;

/**
 * Created by petrkubat on 05/05/16.
 */
public class EncryptedFileStatus {
    private String fileName;
    private boolean isIntegral;

    public EncryptedFileStatus(String fileName, boolean isIntegral) {
        this.fileName = fileName;
        this.isIntegral = isIntegral;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isIntegral() {
        return isIntegral;
    }
}
