package security;

import basic.EncryptionService;
import org.apache.commons.io.IOUtils;
import security.exceptions.EncryptionException;
import security.exceptions.KeyGenerationException;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jan on 16.5.16.
 */
public class AesEncryptionProvider implements EncryptionProviderInterface {

    private static int keySize;
    private static int iterationCount;
    private static final String KEY_GENERATION_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String CIPHER_SPEC = "AES/CBC/PKCS5Padding";
    private static final String KEY_SPEC = "AES";

    public AesEncryptionProvider(int keySize, int iterationCount) {
        this.keySize = keySize;
        this.iterationCount = iterationCount;
    }

    public AesEncryptionProvider() {
        keySize = 128;
        iterationCount = 1546;
    }

    @Override
    public InputStream encryptData(InputStream data, String password, String salt) throws EncryptionException, InvalidParameterSpecException {
        checkParams(data, password, salt);
        return AesOperation(data, password, salt, Cipher.ENCRYPT_MODE);
    }

    @Override
    public InputStream decryptData(InputStream data, String password, String salt) throws EncryptionException, InvalidParameterSpecException {
        checkParams(data, password, salt);
        return AesOperation(data, password, salt, Cipher.DECRYPT_MODE);
    }

    private InputStream AesOperation(InputStream data, String password, String salt, int mode) throws EncryptionException {
        byte[] outputBytes;
        try {
            Cipher cipher = getCipher(mode, password, salt);
            byte[] inputBytes = IOUtils.toByteArray(data);
            outputBytes = cipher.doFinal(inputBytes);
        } catch (IOException e) {
            throw new EncryptionException(e.getMessage()); // IOUtils.toByteArray
        } catch (IllegalBlockSizeException e) {
            throw new EncryptionException("Illegal block size"); // doFinal
        } catch (BadPaddingException e) {
            throw new EncryptionException("Bad padding"); // doFinal
        } catch (KeyGenerationException e) {
            throw new EncryptionException(e.getMessage());
        }
        return new ByteArrayInputStream(outputBytes);
    }

    private Cipher getCipher(int mode, String password, String salt) throws KeyGenerationException, EncryptionException{
        SecretKeySpec key;
        key = getSymetricKey(password, salt);
        IvParameterSpec iv = getIV(key.getEncoded());
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(CIPHER_SPEC);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("No such encryption algorithm: " + CIPHER_SPEC);
        } catch (NoSuchPaddingException e) {
            throw new EncryptionException("No such encryption algorithm: " + CIPHER_SPEC);
        }
        try {
            cipher.init(mode, key, iv);
        } catch (InvalidKeyException e) {
            throw new EncryptionException("Invalid key used");
        } catch (InvalidAlgorithmParameterException e) {
            throw new EncryptionException("Invalid encryption parameters used");
        }
        return cipher;
    }

    private SecretKeySpec getSymetricKey(String passwordString, String saltString) throws KeyGenerationException {
        char[] password = passwordString.toCharArray();
        byte[] salt = saltString.getBytes();
        PBEKeySpec pbeSpec = new PBEKeySpec(password, salt, iterationCount, keySize);
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
        byte[] key_bytes = key.getEncoded();
        SecretKeySpec encKey = new SecretKeySpec(key_bytes, KEY_SPEC);
        return encKey;
    }

    private IvParameterSpec getIV(byte[] seed) {
        return new IvParameterSpec(seed);
    }

    private void checkParams(InputStream file, String password, String salt) throws InvalidParameterSpecException {
        if (file == null || password == null || salt == null) {
            throw new InvalidParameterSpecException("Parameters cannot be null");
        }
        if (password.equals("") || salt.equals("")) {
            throw new InvalidParameterSpecException("Password and salt cannot be empty");
        }
    }
}
