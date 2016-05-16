package filesystem;

import filesystem.exceptions.InvalidPathException;
import filesystem.exceptions.PathCollisionException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by petrkubat on 16/05/16.
 */
public class LocalFileStorage implements FileSystemServiceInterface {
    private Path rootDirPath;

    public LocalFileStorage(String rootDirectory) {
        this.rootDirPath = Paths.get(rootDirectory);
    }

    private Path getResourcePath(String relativePath) throws InvalidPathException {
        Path targetPath = Paths.get(rootDirPath.toString(), relativePath).normalize();

        // Check whether it is still contained within root directory
        if (!targetPath.startsWith(targetPath)) {
            throw new InvalidPathException("Path leads outside of root directory.");
        }

        return targetPath;
    }

    public void initialize() throws IOException {
        // TODO checks
        Files.createDirectories(rootDirPath);
    }

    @Override
    public File createFile(InputStream content, String path) throws InvalidPathException, PathCollisionException, IOException {
        Path targetPath = getResourcePath(path);

        if (Files.exists(targetPath)) {
            throw new PathCollisionException("Target path already contains a file.");
        }

        Files.createDirectories(targetPath.getParent());
        //Files.createFile(targetPath);
        Files.copy(content, targetPath);

        return targetPath.toFile();
    }

    @Override
    public List<File> listFiles(String path) throws InvalidPathException, IOException {
        Path targetPath = getResourcePath(path);

        if (!Files.isDirectory(targetPath)) {
            throw new InvalidPathException("Target is not a folder!");
        }

        return Files.list(targetPath).map(filePath -> filePath.toFile()).collect(Collectors.toList());
    }

    @Override
    public File getFile(String path) throws InvalidPathException {
        Path targetPath = getResourcePath(path);

        if (!Files.exists(targetPath)) {
            throw new InvalidPathException("Target file doesn't exist.");
        }

        return targetPath.toFile();
    }

    @Override
    public void moveFile(String oldPath, String newPath) {

    }

    @Override
    public void deleteFile(String path) throws InvalidPathException, IOException {
        Path targetPath = getResourcePath(path);

        if (!Files.exists(targetPath)) {
            throw new InvalidPathException("Target file doesn't exist.");
        }

        Files.delete(targetPath);
    }
}
