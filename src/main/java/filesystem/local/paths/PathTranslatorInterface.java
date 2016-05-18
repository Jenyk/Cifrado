package filesystem.local.paths;

import filesystem.exceptions.InvalidPathException;

import java.nio.file.Path;

/**
 * Created by petrkubat on 18/05/16.
 */
public interface PathTranslatorInterface {
    String getResourcePublicPath(Path internalPath);
    Path getResourceInternalPath(String publicPath) throws InvalidPathException;
}
