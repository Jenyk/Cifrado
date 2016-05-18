package tests;

import filesystem.FileRecord;
import filesystem.FileSystemServiceInterface;
import filesystem.local.LocalFileStorage;
import filesystem.local.deletion.SimpleFileDeleter;
import org.junit.Test;
import security.HmacIntegrityProvider;
import security.IntegrityProviderInterface;
import security.exceptions.IntegrityException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.spec.InvalidParameterSpecException;
import static org.junit.Assert.*;

/**
 * Created by jan on 18.5.16.
 */
public class IntegrityTest {

    private static final String INTEGRITY_DIRECTORY = "encfs/integrity";
    private static final byte[] TEST_DATA = {1, 2, 3, 4, 5};
    private FileSystemServiceInterface hashFileStorage = new LocalFileStorage(INTEGRITY_DIRECTORY, new SimpleFileDeleter());
    private IntegrityProviderInterface integrityProvider = new HmacIntegrityProvider(hashFileStorage);

    private FileRecord getTestFileRecord(Path testFile, String testPath) {
        return new FileRecord() {
            @Override
            public String getPath() {
                return testPath;
            }

            @Override
            public boolean isDirectory() {
                return false;
            }

            @Override
            public InputStream getStream() throws IOException {
                return Files.newInputStream(testFile);
            }
        };
    }

    @Test
    public void testTracking() {
        String testPath = "testFileIntegrity.txt";
        String password = "password";

        Path testFile = Paths.get(INTEGRITY_DIRECTORY, testPath);

        FileRecord testFileRecord = getTestFileRecord(testFile, testPath);

        try {
            Files.createDirectories(testFile.getParent());
            Files.createFile(testFile);
            integrityProvider.trackNewFile(testFileRecord, password);
            assertTrue(integrityProvider.checkFileIntegrity(testFileRecord, password));

            File hashFile = new File(INTEGRITY_DIRECTORY + "/" + testPath + ".mac");
            assertTrue(hashFile.exists());

            Files.delete(testFile);
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

    @Test
    public void testModify() {
        String testPath = "testFileIntegrityModify.txt";
        String password = "password";

        Path testFile = Paths.get(INTEGRITY_DIRECTORY, testPath);

        FileRecord testFileRecord = getTestFileRecord(testFile, testPath);

        try {
            Files.createDirectories(testFile.getParent());
            Files.createFile(testFile);
            integrityProvider.trackNewFile(testFileRecord, password);
            assertTrue(integrityProvider.checkFileIntegrity(testFileRecord, password));

            Files.write(testFile, TEST_DATA);
            assertFalse(integrityProvider.checkFileIntegrity(testFileRecord, password));
            Files.delete(testFile);
            integrityProvider.stopTrackingFile(testPath);
        } catch (IntegrityException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
