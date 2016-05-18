package tests;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import security.AesEncryptionProvider;
import security.EncryptionProviderInterface;
import security.exceptions.EncryptionException;
import utils.Util;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.spec.InvalidParameterSpecException;

import static org.junit.Assert.*;

/**
 * Created by jan on 18.5.16.
 */
public class EncryptionTest {

    static EncryptionProviderInterface encryptionProvider = new AesEncryptionProvider();

    @Test
    public void testEncDec() {
        encryptionProvider = new AesEncryptionProvider();
        String password = "password";
        String salt = "salt";
        String testString = "test string";

        try {
            InputStream in = IOUtils.toInputStream(testString, "UTF-8");
            InputStream out = encryptionProvider.encryptData(in, password, salt);
            byte[] cipherBytes = IOUtils.toByteArray(out);
            String cipherText = Util.bytesToHexString(cipherBytes);
            assertFalse(testString.equals(cipherText));

            out = new ByteArrayInputStream(cipherBytes);
            InputStream decrypted = encryptionProvider.decryptData(out, password, salt);
            String plaintext = IOUtils.toString(decrypted, "UTF-8");
            assertTrue(testString.equals(plaintext));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }
    }
}
