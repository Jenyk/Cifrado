package filesystem.local.paths;

import filesystem.exceptions.InvalidPathException;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by petrkubat on 18/05/16.
 */
public class SubdirectoryPathTranslator implements PathTranslatorInterface {
    private Path rootDirPath;

    public SubdirectoryPathTranslator(Path rootDirPath) {
        this.rootDirPath = rootDirPath;
    }

    @Override
    public String getResourcePublicPath(Path internalPath) {
        return rootDirPath.relativize(internalPath).toString();
    }

    @Override
    public Path getResourceInternalPath(String publicPath) throws InvalidPathException {
        Path internalPath = Paths.get(rootDirPath.toString(), publicPath).normalize();

        // Check whether it is still contained within root directory
        if (!internalPath.startsWith(rootDirPath)) {
            throw new InvalidPathException("Path leads outside of root directory.");
        }

        return internalPath;
    }
}
