package security.exceptions;

/**
 * Created by jan on 16.5.16.
 */
public class EncryptionException extends Exception {

    public EncryptionException() {
    }

    public EncryptionException(String message) {
        super(message);
    }

    public EncryptionException(Throwable cause) {
        super(cause);
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
