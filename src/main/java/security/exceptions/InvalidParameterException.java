package security.exceptions;

/**
 * Created by petrkubat on 16/05/16.
 */
public class InvalidParameterException extends Exception {
    public InvalidParameterException() {
    }

    public InvalidParameterException(String message) {
        super(message);
    }

    public InvalidParameterException(Throwable cause) {
        super(cause);
    }

    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
