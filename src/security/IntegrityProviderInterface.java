package security;

import java.io.File;

/**
 * Created by petrkubat on 05/05/16.
 */
public interface IntegrityProviderInterface {
    boolean trackNewFile(File file, String password);
    boolean stopTrackingFile(File file);
    boolean checkFileIntegrity(File file, String password);
}
