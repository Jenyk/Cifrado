package tests;

import control.RawFile;
import filesystem.FileRecord;
import filesystem.FileSystemServiceInterface;
import filesystem.exceptions.FileSystemException;
import filesystem.exceptions.InvalidPathException;
import filesystem.exceptions.PathCollisionException;
import filesystem.local.LocalFileStorage;
import filesystem.local.deletion.SecureFileDeleter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by jan on 18.5.16.
 */
public class FilesystemTest {
    private static final String DATA_DIRECTORY = "encfs/data";

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private FileSystemServiceInterface initFileSystem(String directory) throws FileSystemException, IOException {
        Path target = Paths.get(directory);

        FileUtils.deleteDirectory(target.toFile());

        FileSystemServiceInterface fileService = new LocalFileStorage(target.toString(), new SecureFileDeleter());

        fileService.initialize();

        return fileService;
    }

    private void createBasicSetOfFiles(FileSystemServiceInterface fileService) throws Exception {
        byte[] source = {0, 1, 2, 3, 4, 5};
        ByteArrayInputStream bis = new ByteArrayInputStream(source);

        fileService.createFile(bis, "Dir1/Dir2/File.txt");

        byte[] source2 = {0, 0, 0, 0, 0, 0};
        ByteArrayInputStream bis2 = new ByteArrayInputStream(source2);

        fileService.createFile(bis2, "Dir1/Dir2/File2.txt");

        byte[] source3 = {0, 0, 0, 1, 1, 1};
        ByteArrayInputStream bis3 = new ByteArrayInputStream(source3);

        fileService.createFile(bis3, "Dir1/FileXXX.txt");

        byte[] source4 = {0, 0, 0, 1, 1, 1};
        ByteArrayInputStream bis4 = new ByteArrayInputStream(source4);

        fileService.createFile(bis4, "Dir1/FileXYZ.txt");
    }

    @Test
    public void createExistingFile() throws Exception {
        FileSystemServiceInterface fileService = initFileSystem(DATA_DIRECTORY);
        createBasicSetOfFiles(fileService);

        byte[] source = {0, 1, 2, 3, 4, 5};
        ByteArrayInputStream bis = new ByteArrayInputStream(source);

        exception.expect(PathCollisionException.class);
        fileService.createFile(bis, "Dir1/FileXXX.txt");
    }

    @Test
    public void listNonExcistingDirectory() throws  Exception {
        FileSystemServiceInterface fileService = initFileSystem(DATA_DIRECTORY);
        createBasicSetOfFiles(fileService);

        exception.expect(InvalidPathException.class);
        List<FileRecord> list3 = fileService.listFiles("Dir1/FileXXX.txt");
    }

    @Test
    public void listDirectories() throws Exception {
        FileSystemServiceInterface fileService = initFileSystem(DATA_DIRECTORY);
        createBasicSetOfFiles(fileService);

        List<FileRecord> list1 = fileService.listFiles("Dir1");
        List<FileRecord> list2 = fileService.listFiles("Dir1/Dir2");

        assertTrue(list1.size() == 3);
        assertTrue(list2.size() == 2);
    }

    @Test
    public void readFile() throws Exception {
        FileSystemServiceInterface fileService = initFileSystem(DATA_DIRECTORY);

        byte[] source = {0, 1, 2, 3, 4, 5};
        ByteArrayInputStream bis = new ByteArrayInputStream(source);

        String path = "Dir1/Dir2/File.txt";

        fileService.createFile(bis, path);

        FileRecord file = fileService.getFile(path);

        byte[] result = IOUtils.toByteArray(file.getStream());

        assertTrue(Arrays.equals(source, result));
    }

    @Test
    public void deleteExistingFile() throws Exception {
        FileSystemServiceInterface fileService = initFileSystem(DATA_DIRECTORY);
        createBasicSetOfFiles(fileService);

        String path = "Dir1/FileXXX.txt";

        assertTrue(fileService.listFiles("Dir1").size() == 3);

        fileService.deleteFile(path);

        assertTrue(fileService.listFiles("Dir1").size() == 2);

        exception.expect(InvalidPathException.class);
        fileService.getFile(path);
    }

    @Test
    public void deleteNonExistingFile() throws Exception {
        FileSystemServiceInterface fileService = initFileSystem(DATA_DIRECTORY);
        createBasicSetOfFiles(fileService);

        String path = "Dir1/FileXXXXZ.txt";

        exception.expect(InvalidPathException.class);
        fileService.deleteFile(path);
    }

    @Test
    public void createDirectory() throws Exception {
        FileSystemServiceInterface fileService = initFileSystem(DATA_DIRECTORY);
        createBasicSetOfFiles(fileService);

        assertTrue(fileService.listFiles("Dir1").size() == 3);

        fileService.createDirectory("Dir1/Dir8");

        assertTrue(fileService.listFiles("Dir1").size() == 4);
    }

    @Test
    public void deleteDirectory() throws Exception {
        FileSystemServiceInterface fileService = initFileSystem(DATA_DIRECTORY);
        createBasicSetOfFiles(fileService);

        assertTrue(fileService.listFiles("").size() == 1);

        fileService.deleteDirectory("Dir1");

        assertTrue(fileService.listFiles("").size() == 0);

        exception.expect(InvalidPathException.class);
        fileService.getFile("Dir1/Dir2/File2.txt");
    }
}
