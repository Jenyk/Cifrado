package filesystem.local;

import filesystem.FileRecord;
import filesystem.FileSystemServiceInterface;
import filesystem.exceptions.FileSystemException;
import filesystem.exceptions.InvalidPathException;
import filesystem.exceptions.PathCollisionException;
import filesystem.local.deletion.FileDeleterInterface;
import filesystem.local.paths.PathTranslatorInterface;
import filesystem.local.paths.SubdirectoryPathTranslator;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by petrkubat on 16/05/16.
 */
public class LocalFileStorage implements FileSystemServiceInterface {
    private Path rootDirPath;
    private PathTranslatorInterface pathTranslator;
    private FileDeleterInterface fileDeleter;

    public LocalFileStorage(String rootDirectory, FileDeleterInterface fileDeleter) {
        this.rootDirPath = Paths.get(rootDirectory);
        this.pathTranslator = new SubdirectoryPathTranslator(rootDirPath);
        this.fileDeleter = fileDeleter;
    }

    public void initialize() throws FileSystemException {
        // TODO checks
        try {
            Files.createDirectories(rootDirPath);
        } catch (IOException ex) {
            throw new FileSystemException(ex);
        }
    }

    @Override
    public FileRecord createFile(InputStream content, String path) throws InvalidPathException, PathCollisionException, FileSystemException {
        Path targetPath = pathTranslator.getResourceInternalPath(path);

        if (Files.exists(targetPath)) {
            throw new PathCollisionException("Target path already contains a file.");
        }

        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(content, targetPath);
        } catch (IOException ex) {
            throw new FileSystemException(ex);
        }

        return new LocalFileRecord(path, targetPath);
    }

    private List<FileRecord> listFiles(String path, boolean recursive) throws InvalidPathException, FileSystemException {
        Path targetPath = pathTranslator.getResourceInternalPath(path);

        if (!Files.isDirectory(targetPath)) {
            throw new InvalidPathException("Target is not a folder!");
        }

        try {
            Stream<Path> files = recursive ? Files.walk(targetPath) : Files.list(targetPath);
            return files.map(filePath ->
                    new LocalFileRecord(pathTranslator.getResourcePublicPath(filePath), filePath)
            ).collect(Collectors.toList());
        } catch (IOException ex) {
            throw new FileSystemException(ex);
        }
    }

    @Override
    public List<FileRecord> listFiles(String path) throws InvalidPathException, FileSystemException {
        return listFiles(path, false);
    }

    @Override
    public List<FileRecord> listFilesRecursive(String path) throws InvalidPathException, FileSystemException {
        return listFiles(path, true);
    }

    @Override
    public FileRecord getFile(String path) throws InvalidPathException {
        Path targetPath = pathTranslator.getResourceInternalPath(path);

        if (!Files.exists(targetPath)) {
            throw new InvalidPathException("Target file doesn't exist.");
        }

        return new LocalFileRecord(path, targetPath);
    }

    @Override
    public void deleteFile(String path) throws InvalidPathException, FileSystemException {
        Path targetPath = pathTranslator.getResourceInternalPath(path);

        if (!Files.exists(targetPath)) {
            throw new InvalidPathException("Target file doesn't exist.");
        }

        if (!Files.isRegularFile(targetPath)) {
            throw new InvalidPathException("Target isn't a file.");
        }

        try {
            fileDeleter.deleteFile(targetPath);
        } catch (IOException ex) {
            throw new FileSystemException(ex);
        }
    }

    @Override
    public void createDirectory(String path) throws InvalidPathException, PathCollisionException, FileSystemException {
        Path targetPath = pathTranslator.getResourceInternalPath(path);

        if (Files.exists(targetPath)) {
            throw new PathCollisionException("Target path already contains a file or directory.");
        }

        try {
            Files.createDirectories(targetPath);
        } catch (IOException ex) {
            throw new FileSystemException(ex);
        }
    }

    @Override
    public void deleteDirectory(String path) throws InvalidPathException, FileSystemException {
        Path targetPath = pathTranslator.getResourceInternalPath(path);

        if (!Files.exists(targetPath)) {
            throw new InvalidPathException("Target directory doesn't exist.");
        }

        if (!Files.isDirectory(targetPath)) {
            throw new InvalidPathException("Target isn't a directory.");
        }

        try {
            // Crawl the directory and delete all the files
            for (Path file : Files.walk(targetPath).collect(Collectors.toList())) {
                if (!Files.isDirectory(file)) {
                    fileDeleter.deleteFile(file);
                }
            }

            FileUtils.deleteDirectory(targetPath.toFile());
        } catch (IOException ex) {
            throw new FileSystemException(ex);
        }
    }
}
