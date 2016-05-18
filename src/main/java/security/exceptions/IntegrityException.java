package security.exceptions;

/**
 * Created by jan on 16.5.16.
 */
public class IntegrityException extends Exception {

    public IntegrityException() {
    }

    public IntegrityException(String message) {
        super(message);
    }

    public IntegrityException(Throwable cause) {
        super(cause);
    }

    public IntegrityException(String message, Throwable cause) {
        super(message, cause);
    }
}
