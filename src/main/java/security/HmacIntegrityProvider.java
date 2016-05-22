package security;

import filesystem.FileRecord;
import filesystem.FileSystemServiceInterface;
import filesystem.exceptions.FileSystemException;
import filesystem.local.LocalFileStorage;
import filesystem.exceptions.InvalidPathException;
import org.apache.commons.io.IOUtils;
import security.exceptions.KeyGenerationException;
import filesystem.exceptions.PathCollisionException;
import org.apache.commons.io.FileUtils;
import security.exceptions.IntegrityException;
import utils.Util;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

/**
 * Created by jan on 16.5.16.
 */
public class HmacIntegrityProvider implements IntegrityProviderInterface {

    private FileSystemServiceInterface fileSystem;
    private static final String MAC_ALGORITHM = "HmacSHA1";
    private static final String KEY_GENERATION_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String EXTENSION = ".mac";
    private static final String TEMP_PATH = "temp";

    public HmacIntegrityProvider(FileSystemServiceInterface fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public void trackNewFile(FileRecord file, String password) throws IntegrityException, InvalidParameterSpecException {
        String targetPath = file.getPath();
        checkParams(file, password, targetPath);
        targetPath += EXTENSION;
        try {
            // compute mas
            String mac = getMAC(file, password, targetPath);
            // save to temp. file
            File temp = new File(TEMP_PATH + EXTENSION);
            FileUtils.writeStringToFile(temp, mac, "UTF-8");
            // temp file to input stream
            InputStream hashStream = new FileInputStream(temp);
            // save mac data
            fileSystem.createFile(hashStream, targetPath);
            // remove temp file
            temp.delete();
        } catch (IOException e) {
            throw new IntegrityException(e.getMessage());
        } catch (FileSystemException e) {
            throw new IntegrityException(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new IntegrityException("No such MAC algorithm: " + MAC_ALGORITHM);
        } catch (PathCollisionException e) {
            throw new IntegrityException("Target MAC path already contains a file.");
        } catch (InvalidPathException e) {
            throw new IntegrityException(e.getMessage());
        } catch (KeyGenerationException e) {
            throw new IntegrityException(e.getMessage());
        }
    }

    @Override
    public void stopTrackingFile(String path) throws IntegrityException, InvalidParameterSpecException {
        if (path == null) {
            throw new InvalidParameterSpecException("Parameter path cannot be null");
        }
        path += EXTENSION;
        try {
            // delete mac file
            fileSystem.deleteFile(path);
        } catch (InvalidPathException e) {
            throw new IntegrityException(e.getMessage());
        } catch (FileSystemException e) {
            throw new IntegrityException(e.getMessage());
        }
    }

    @Override
    public boolean checkFileIntegrity(FileRecord file, String password) throws IntegrityException, InvalidParameterSpecException {
        String path = file.getPath();
        checkParams(file, password, path);
        path += EXTENSION;
        try {
            // compute new mac
            String newMac = getMAC(file, password, path);
            // get saved mac from file
            FileRecord macFile = fileSystem.getFile(path);
            try (BufferedReader macReader = new BufferedReader(new InputStreamReader(macFile.getStream()))) {
                String oldMac = macReader.readLine();
                // compare
                return oldMac.equals(newMac);
            }
        } catch (IOException e) {
            throw new IntegrityException(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            throw new IntegrityException("No such MAC algorithm: " + MAC_ALGORITHM);
        } catch (InvalidPathException e) {
            throw new IntegrityException(e.getMessage());
        } catch (KeyGenerationException e) {
            throw new IntegrityException(e.getMessage());
        }
    }

    private String getMAC(FileRecord file, String password, String salt) throws KeyGenerationException, IntegrityException, NoSuchAlgorithmException, IOException {
        Mac mac = Mac.getInstance(MAC_ALGORITHM);
        byte[] m;
        try (InputStream stream = file.getStream()) {
            m = IOUtils.toByteArray(stream);
        }
        // salt = path
        Key key = getKey(password, salt);
        try {
            mac.init(key);
        } catch (InvalidKeyException e) {
            throw new IntegrityException("Invalid key used");
        }
        mac.update(m);
        byte[] auth = mac.doFinal();
        return Util.bytesToHexString(auth);
    }

    private Key getKey(String pass, String saltString) throws KeyGenerationException {
        byte[] salt = saltString.getBytes();
        char[] password = pass.toCharArray();
        PBEKeySpec pbeSpec = new PBEKeySpec(password, salt, 2048, 128);
        SecretKeyFactory keyFact;
        try {
            keyFact = SecretKeyFactory.getInstance(KEY_GENERATION_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new KeyGenerationException("No such key generation algorithm: " + KEY_GENERATION_ALGORITHM);
        }
        Key key;
        try {
            key = keyFact.generateSecret(pbeSpec);
        } catch (InvalidKeySpecException e) {
            throw new KeyGenerationException("Invalid PBEKeySpec: " + e.getMessage());
        }
        return key;
    }

    private void checkParams(FileRecord file, String password, String salt) throws InvalidParameterSpecException {
        if (file == null || password == null || salt == null) {
            throw new InvalidParameterSpecException("Parameters cannot be null");
        }
        if (password.equals("") || salt.equals("")) {
            throw new InvalidParameterSpecException("Password and salt cannot be empty");
        }
    }
}
