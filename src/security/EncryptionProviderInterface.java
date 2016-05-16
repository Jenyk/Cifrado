package security;

import java.io.InputStream;

/**
 * Created by petrkubat on 05/05/16.
 */
public interface EncryptionProviderInterface {
    InputStream encryptData(InputStream data, String password, String salt) throws EncryptionException;
    InputStream decryptData(InputStream data, String password, String salt) throws EncryptionException;
}
