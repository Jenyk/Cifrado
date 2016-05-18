package security;

import security.exceptions.EncryptionException;
import java.io.InputStream;
import java.security.spec.InvalidParameterSpecException;

/**
 * Created by petrkubat on 05/05/16.
 */
public interface EncryptionProviderInterface {

    InputStream encryptData(InputStream data, String password, String salt) throws EncryptionException, InvalidParameterSpecException;

    InputStream decryptData(InputStream data, String password, String salt) throws EncryptionException, InvalidParameterSpecException;
}
