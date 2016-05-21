package tests;

import basic.EncryptionService;
import control.EncryptionServiceInterface;
import control.RawFile;
import filesystem.FileSystemServiceInterface;
import filesystem.local.LocalFileStorage;
import filesystem.local.deletion.FileDeleterInterface;
import filesystem.local.deletion.SecureFileDeleter;
import filesystem.local.deletion.SimpleFileDeleter;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import security.AesEncryptionProvider;
import security.EncryptionProviderInterface;
import security.HmacIntegrityProvider;
import security.IntegrityProviderInterface;
import org.junit.Test;
import java.io.*;
import static org.junit.Assert.*;


/**
 * Created by jan on 18.5.16.
 */
public class GlobalTest {

    private static final String DATA_DIRECTORY = "encfs/data";
    private static final String INTEGRITY_DIRECTORY = "encfs/integrity";
    private EncryptionProviderInterface encryptionProvider = new AesEncryptionProvider();
    private FileSystemServiceInterface fileSystemService = new LocalFileStorage(DATA_DIRECTORY, new SecureFileDeleter());
    private FileSystemServiceInterface hashFileStorage = new LocalFileStorage(INTEGRITY_DIRECTORY, new SimpleFileDeleter());
    private IntegrityProviderInterface integrityProvider = new HmacIntegrityProvider(hashFileStorage);
    EncryptionServiceInterface service = new EncryptionService(fileSystemService, encryptionProvider, integrityProvider);

    @Test
    public void testAddGetDelete() {
        String testPath = "testFolder/testFileGlobal.txt";
        String testFileName = "testFileGlobal.txt";
        String password = "password";
        String testString = "Hello world";

        try {
            // Add file
            File temp = new File("temp.file");
            InputStream tempStream;
            FileUtils.writeStringToFile(temp, testString, "UTF-8");
            tempStream = new FileInputStream(temp);
            RawFile newFile = new RawFile(testFileName, tempStream);
            service.addFile(newFile, testPath, password);
            temp.delete();

            // Get file
            RawFile recoveredRawFile = service.getFile(testPath, password);
            File recoveredFile = new File("recovered.txt");
            FileUtils.copyInputStreamToFile(recoveredRawFile.getData(), recoveredFile);
            BufferedReader macReader = new BufferedReader(new FileReader(recoveredFile));
            String recoveredLine = macReader.readLine();
            assertTrue(testString.equals(recoveredLine));

            // Delete file
            recoveredFile.delete();
            service.deleteFile(testPath, password);
            File data = new File(DATA_DIRECTORY + "/" + testPath);
            assertFalse(data.exists());
            File mac = new File(DATA_DIRECTORY + "/" + testPath + ".mac");
            assertFalse(mac.exists());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void testMoveFile() {
        String testPath1 = "testFolder1/testFileGlobalMove1.txt";
        String testPath2 = "testFolder2/testFileGlobalMove2.txt";
        String testFileName = "testFileGlobal.txt"; // useless
        String password = "passwordMove";
        String testString = "Hello world Move";
        try {
            // Add file
            File temp = new File("temp.file");
            InputStream tempStream;
            FileUtils.writeStringToFile(temp, testString, "UTF-8");
            tempStream = new FileInputStream(temp);
            RawFile newFile = new RawFile(testFileName, tempStream);
            service.addFile(newFile, testPath1, password);

            File assertDataFile1 = new File(DATA_DIRECTORY + "/" + testPath1);
            assertTrue(assertDataFile1.exists());
            File assertMacFile1 = new File(DATA_DIRECTORY + "/" + testPath1 + ".mac");
            assertTrue(assertMacFile1.exists());
            File assertDataFile2 = new File(DATA_DIRECTORY + "/" + testPath2);
            assertFalse(assertDataFile2.exists());
            File assertMacFile2 = new File(DATA_DIRECTORY + "/" + testPath2 + ".mac");
            assertFalse(assertMacFile2.exists());


            // Move file
            service.moveFile(testPath1, testPath2, password);

            assertDataFile1 = new File(DATA_DIRECTORY + "/" + testPath1);
            assertFalse(assertDataFile1.exists());
            assertMacFile1 = new File(DATA_DIRECTORY + "/" + testPath1 + ".mac");
            assertFalse(assertMacFile1.exists());
            assertDataFile2 = new File(DATA_DIRECTORY + "/" + testPath2);
            assertTrue(assertDataFile2.exists());
            assertMacFile2 = new File(DATA_DIRECTORY + "/" + testPath2 + ".mac");
            assertTrue(assertMacFile2.exists());


            // cleanup
            service.deleteFile(testPath2, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Ignore
    @Test
    public void testListFiles() {

    }
}
