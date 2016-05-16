package filesystem.exceptions;

/**
 * Created by petrkubat on 16/05/16.
 */
public class PathCollisionException extends Exception {
    public PathCollisionException() {
    }

    public PathCollisionException(String message) {
        super(message);
    }

    public PathCollisionException(Throwable cause) {
        super(cause);
    }

    public PathCollisionException(String message, Throwable cause) {
        super(message, cause);
    }
}
