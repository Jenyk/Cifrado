package filesystem.exceptions;

/**
 * Created by petrkubat on 20/05/16.
 */
public class FileSystemException extends Exception {
    public FileSystemException() {
    }

    public FileSystemException(String message) {
        super(message);
    }

    public FileSystemException(Throwable cause) {
        super(cause);
    }

    public FileSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
