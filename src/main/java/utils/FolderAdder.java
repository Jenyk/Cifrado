package utils;

import control.EncryptionServiceInterface;
import control.RawFile;
import filesystem.exceptions.FileSystemException;
import filesystem.exceptions.InvalidPathException;
import filesystem.exceptions.PathCollisionException;
import security.exceptions.EncryptionException;
import security.exceptions.IntegrityException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.spec.InvalidParameterSpecException;
import java.util.stream.Collectors;

/**
 * Created by petrkubat on 28/05/16.
 */
public class FolderAdder implements FolderAdderInterface {

    private EncryptionServiceInterface encryptionService;

    public FolderAdder(EncryptionServiceInterface encryptionService) {
        this.encryptionService = encryptionService;
    }

    @Override
    public void addFolder(Path localFolderPath, String targetFolderPath, String password) throws IOException, PathCollisionException,
            EncryptionException, IntegrityException, FileSystemException, InvalidPathException, InvalidParameterSpecException {
        for (Path file : Files.walk(localFolderPath).filter(f -> !Files.isDirectory(f)).collect(Collectors.toList())) {
            Path relativePath = localFolderPath.relativize(file);
            Path targetFilePath = Paths.get(targetFolderPath, relativePath.toString());

            RawFile newFile = new RawFile(targetFilePath.toString(), Files.newInputStream(file));

            encryptionService.addFile(newFile, targetFilePath.toString(), password);
        }
    }

    @Override
    public void removeFolder(Path path) throws IOException, InvalidPathException, IntegrityException, InvalidParameterSpecException, FileSystemException {
        for (Path file : Files.walk(path).filter(f -> !Files.isDirectory(f)).collect(Collectors.toList())) {
            File target = file.toFile();
            encryptionService.deleteFile(target.getPath());
        }
        for (Path file : Files.walk(path).filter(f -> Files.isDirectory(f)).collect(Collectors.toList())) {
            File target = file.toFile();
            encryptionService.deleteDirectory(target.getPath());
        }
    }
}
