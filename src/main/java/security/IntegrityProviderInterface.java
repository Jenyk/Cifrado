package security;

import security.exceptions.IntegrityException;
import java.io.File;
import java.security.spec.InvalidParameterSpecException;

/**
 * Created by petrkubat on 05/05/16.
 */
public interface IntegrityProviderInterface {

    void trackNewFile(File file, String password, String targetPath) throws IntegrityException, InvalidParameterSpecException;

    void stopTrackingFile(String path) throws IntegrityException, InvalidParameterSpecException;

    boolean checkFileIntegrity(File file, String password, String path) throws IntegrityException, InvalidParameterSpecException;
}
