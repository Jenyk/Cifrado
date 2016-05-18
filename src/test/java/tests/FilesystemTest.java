package tests;

import filesystem.FileSystemServiceInterface;
import filesystem.local.LocalFileStorage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by jan on 18.5.16.
 */
public class FilesystemTest {
    /*
    private static final String DATA_DIRECTORY = "encfs/data";

    private FileSystemServiceInterface initFileSystem(String directory) throws IOException {
        Path target = Paths.get(directory);

        FileSystemServiceInterface fileService = new LocalFileStorage(target.toString());

        fileService.initialize();

        return fileService;
    }

    private static boolean localFileStorageTest() {

        try {



            byte[] source = {0, 1, 2, 3, 4, 5};
            ByteArrayInputStream bis = new ByteArrayInputStream(source);

            fileService.createFile(bis, "Dir1/Dir2/File.txt");

            byte[] source2 = {0, 0, 0, 0, 0, 0};
            ByteArrayInputStream bis2 = new ByteArrayInputStream(source);

            fileService.createFile(bis2, "Dir1/Dir2/File2.txt");

            byte[] source3 = {0, 0, 0, 1, 1, 1};
            ByteArrayInputStream bis3 = new ByteArrayInputStream(source);

            fileService.createFile(bis3, "Dir1/FileXXX.txt");

            byte[] source4 = {0, 1, 2, 3, 4, 5};
            ByteArrayInputStream bis4 = new ByteArrayInputStream(source);

            boolean threw = false;
            try {
                fileService.createFile(bis4, "Dir1/FileXXX.txt");
            }
            catch (Exception ex) {
                threw = true;
            }

            if (!threw) return false;

            List<File> list1 = fileService.listFiles("Dir1");
            List<File> list2 = fileService.listFiles("Dir1/Dir2");

            threw = false;
            try {
                List<File> list3 = fileService.listFiles("Dir1/FileXXX.txt");
            }
            catch (Exception ex) {
                threw = true;
            }

            fileService.deleteFile("Dir1/FileXXX.txt");

            threw = false;
            try {
                fileService.deleteFile("Dir1/FileXXX.txt");
            }
            catch (Exception ex) {
                threw = true;
            }


            //Files.delete(target);

            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    */
}
