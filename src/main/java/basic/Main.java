package basic;

import org.apache.commons.io.IOUtils;
import security.AesEncryptionProvider;
import security.exceptions.EncryptionException;
import security.EncryptionProviderInterface;
import java.io.IOException;
import java.io.InputStream;


/**
 * Created by jan on 5.5.16.
 */
public class Main {

    static EncryptionProviderInterface test;

    public static void main(String[] args) {
        test = new AesEncryptionProvider();
        String password = "pass";
        String salt = "salt";
        String testString = "test string";

        try {
            System.out.println(testString);
            InputStream in = IOUtils.toInputStream(testString, "UTF-8");
            InputStream out = test.encryptData(in, password, salt);

            InputStream decrypted = test.decryptData(out, password, salt);
            System.out.println(IOUtils.toString(decrypted, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncryptionException e) {
            e.printStackTrace();
        }
    }
}
