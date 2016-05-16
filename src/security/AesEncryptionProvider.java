package security;

import org.apache.commons.io.IOUtils;
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

/**
 * Created by jan on 16.5.16.
 */
public class AesEncryptionProvider implements EncryptionProviderInterface {

    private static int keySize;
    private static int iterationCount;
    private static String keyGenerationAlgorithm = "PBKDF2WithHmacSHA1";
    private static String cipherSpec;

    public AesEncryptionProvider(int keySize, int iterationCount/*, String encryptionMode*/) {
        this.keySize = keySize;
        this.iterationCount = iterationCount;
//        this.cipherSpec = "AES/" + encryptionMode + "/PKCS5Padding";
        cipherSpec = "AES/CBC/PKCS5Padding";
    }

    public AesEncryptionProvider() {
        keySize = 128;
        iterationCount = 1546;
        cipherSpec = "AES/CBC/PKCS5Padding";
    }


    @Override
    public InputStream encryptData(InputStream data, String password, String salt) throws EncryptionException {
        return AesOperation(data, password, salt, Cipher.ENCRYPT_MODE);
    }

    @Override
    public InputStream decryptData(InputStream data, String password, String salt) throws EncryptionException {
        return AesOperation(data, password, salt, Cipher.DECRYPT_MODE);
    }

    private InputStream AesOperation(InputStream data, String password, String salt, int mode) throws EncryptionException {
        byte[] outputBytes;
        try {
            Cipher cipher = getCipher(mode, password, salt);
            byte[] inputBytes = IOUtils.toByteArray(data);
            outputBytes = cipher.doFinal(inputBytes);
        } catch (InvalidKeySpecException e) {
            throw new EncryptionException(e.getMessage()); // getCipher
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException(e.getMessage()); // getCipher
        } catch (NoSuchPaddingException e) {
            throw new EncryptionException(e.getMessage()); // getCipher
        } catch (InvalidAlgorithmParameterException e) {
            throw new EncryptionException(e.getMessage()); // getCipher
        } catch (InvalidKeyException e) {
            throw new EncryptionException(e.getMessage()); // getCipher
        } catch (IOException e) {
            throw new EncryptionException(e.getMessage()); // IOUtils.toByteArray
        } catch (IllegalBlockSizeException e) {
            throw new EncryptionException(e.getMessage()); // doFinal
        } catch (BadPaddingException e) {
            throw new EncryptionException(e.getMessage()); // doFinal
        }
        return new ByteArrayInputStream(outputBytes);
    }

    private Cipher getCipher(int mode, String password, String salt) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        SecretKeySpec key;
        key = getSymetricKey(password, salt);
        IvParameterSpec iv = getIV(key.getEncoded());
        Cipher cipher = Cipher.getInstance(cipherSpec);
        cipher.init(mode, key, iv);
        return cipher;
    }

    private SecretKeySpec getSymetricKey(String passwordString, String saltString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] password = passwordString.toCharArray();
        byte[] salt = saltString.getBytes();
        PBEKeySpec pbeSpec = new PBEKeySpec(password, salt, iterationCount, keySize);
        SecretKeyFactory keyFact = SecretKeyFactory.getInstance(keyGenerationAlgorithm);
        Key key = keyFact.generateSecret(pbeSpec);
        byte[] key_bytes = key.getEncoded();
        SecretKeySpec encKey = new SecretKeySpec(key_bytes, "AES");
        return encKey;
    }

    private IvParameterSpec getIV(byte[] seed) {
        return new IvParameterSpec(seed);
    }
}
