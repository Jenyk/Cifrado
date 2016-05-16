package utils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by jan on 21.4.16.
 */
public class Hash {

    public static void main(String[] args) {
        try {

            //Algoritmo para generar las claves a partir de las contraseñas
            String algo = "PBKDF2WithHmacSHA1";

            //Tamaño de la clave de salida en bits
            int size = 128;

            //Contraseña como un String
            String pass = "73E163B34C19B2DC";

            //La password la tenemos que pasar como un char[]
            char[] password = pass.toCharArray();

            //Creo una salt aleatoria
//        byte[] salt = Util.mensajeAleatorio(8);
            byte[] salt = Util.hexStringToBytes("5CE3C90AC5568DFC");

            //Defino un número aleatorio de iteraciones, máximo 2048
//        int iterationCount = Util.numeroAleatorio(2048);
            int iterationCount = 1546;

            //Defino la PBEKeySpec en funcion de los parámetros aleatorios
            PBEKeySpec pbeSpec = new PBEKeySpec(password, salt, iterationCount, size);

            //Creo un secret factory para el algoritmo PBKDF2WithHmacSHA1
            SecretKeyFactory keyFact = SecretKeyFactory.getInstance(algo);

            //Genero la clave y la guardo en el objeto key
            Key key = keyFact.generateSecret(pbeSpec);

            long startTime = System.currentTimeMillis();
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

            //Lo inializo con la clave, puedo usar la clave key o encKey indistintamente
            sha256_HMAC.init(key);

            Path path = Paths.get("file.txt");
            byte[] m = Files.readAllBytes(path);

            //Le paso el mensaje con el método udpate
            sha256_HMAC.update(m);
            long estimatedTime = System.currentTimeMillis() - startTime;
            System.out.println(estimatedTime);

            startTime = System.currentTimeMillis();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(m);
            //Creo un objeto para cifrar
            Cipher cifrado = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] key_bytes = key.getEncoded();

            //Convierto la clave generada a una clave AES
            SecretKeySpec encKey = new SecretKeySpec(key_bytes, "AES");

            //Elejimos un IV de forma aleatoria y lo pasamos an un IvParameterSpec
//        byte[] iv_byte = Util.mensajeAleatorio(16);
            byte[] iv_byte = key_bytes;
            IvParameterSpec iv = new IvParameterSpec(iv_byte);

            //Inicializamos el cifrador en modo cifrado con la clave generada y el IV aleatorio
            cifrado.init(Cipher.ENCRYPT_MODE, encKey, iv);
            estimatedTime = System.currentTimeMillis() - startTime;
            System.out.println(estimatedTime);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

    }
}
