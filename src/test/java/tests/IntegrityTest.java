package tests;

import org.junit.Test;
import security.HmacIntegrityProvider;
import security.IntegrityProviderInterface;
import security.exceptions.IntegrityException;
import java.io.File;
import java.io.IOException;
import java.security.spec.InvalidParameterSpecException;
import static org.junit.Assert.*;

/**
 * Created by jan on 18.5.16.
 */
public class IntegrityTest {

    private static final String INTEGRITY_DIRECTORY = "encfs/integrity";
    IntegrityProviderInterface integrityProvider = new HmacIntegrityProvider(INTEGRITY_DIRECTORY);

    @Test
    public void testTracking() {
        String testPath = "testFileIntegrity.txt";
        String password = "password";
        File testFile = new File(INTEGRITY_DIRECTORY + "/" + testPath);
        try {
            testFile.createNewFile();
            integrityProvider.trackNewFile(testFile, password, testPath);
            assertTrue(integrityProvider.checkFileIntegrity(testFile, password, testPath));

            File hashFile = new File(INTEGRITY_DIRECTORY + "/" + testPath + ".mac");
            assertTrue(hashFile.exists());

            testFile.delete();
            integrityProvider.stopTrackingFile(testPath);
            hashFile = new File(INTEGRITY_DIRECTORY + "/" + testPath + ".mac");
            assertFalse(hashFile.exists());
        } catch (IntegrityException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
