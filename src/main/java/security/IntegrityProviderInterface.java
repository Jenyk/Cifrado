package security;

import filesystem.FileRecord;
import security.exceptions.IntegrityException;
import java.io.File;
import java.security.spec.InvalidParameterSpecException;

/**
 * Created by petrkubat on 05/05/16.
 */
public interface IntegrityProviderInterface {

    void trackNewFile(FileRecord file, String password) throws IntegrityException, InvalidParameterSpecException;

    void stopTrackingFile(String path) throws IntegrityException, InvalidParameterSpecException;

    boolean checkFileIntegrity(FileRecord file, String password) throws IntegrityException, InvalidParameterSpecException;
}
