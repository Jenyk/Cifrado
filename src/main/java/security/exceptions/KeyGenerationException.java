package security.exceptions;

/**
 * Created by jan on 18.5.16.
 */
public class KeyGenerationException extends Exception {

    public KeyGenerationException() {
    }

    public KeyGenerationException(String message) {
        super(message);
    }

    public KeyGenerationException(Throwable cause) {
        super(cause);
    }

    public KeyGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
