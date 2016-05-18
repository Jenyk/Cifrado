package filesystem.local.deletion;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by petrkubat on 18/05/16.
 */
public class SecureFileDeleter implements FileDeleterInterface {
    private static final int OVERWRITE_COUNT = 4;
    private static final int[] VALUES = {0, 0xFF};

    private void overwriteFile(Path filePath, int value) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "rws");
        long length = file.length();

        file.seek(0);

        for (long i = 0; i < length; i++) {
            file.writeByte(value);
        }

        file.close();
    }

    @Override
    public void deleteFile(Path filePath) throws IOException {
        for (int i = 0; i < OVERWRITE_COUNT; i++) {
            overwriteFile(filePath, VALUES[i % VALUES.length]);
        }

        Files.delete(filePath);
    }
}
