package utils;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author isaac
 */
public class P3 {
    public static void main(String[] args) throws Exception {


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

        //Guardo los bytes de la clave en formato RAW en un byte[]
        byte[] key_bytes = key.getEncoded();

        //Muestro como un String Hexadecimal la clave generada
        System.out.println("Key " + Util.bytesToHexString(key_bytes));

        //Creo un objeto para cifrar
        Cipher cifrado = Cipher.getInstance("AES/CBC/PKCS5Padding");

        //Convierto la clave generada a una clave AES
        SecretKeySpec encKey = new SecretKeySpec(key_bytes, "AES");

        //Elejimos un IV de forma aleatoria y lo pasamos an un IvParameterSpec
//        byte[] iv_byte = Util.mensajeAleatorio(16);
        byte[] iv_byte = key_bytes;
        IvParameterSpec iv = new IvParameterSpec(iv_byte);

        //Inicializamos el cifrador en modo cifrado con la clave generada y el IV aleatorio
        cifrado.init(Cipher.DECRYPT_MODE, encKey, iv);

        //Inicializamos el mensaje a cifrar m
//        byte[] m = Util.stringToBytes("Hola Mundo");
        Path path = Paths.get("secreto.docx.enc");
        byte[] m = Files.readAllBytes(path);

        //Ciframos el mensaje m
        byte[] c = cifrado.doFinal(m);

        //Muestro el mensaje cifrado por consola
        System.out.println("Encryption " + Util.bytesToHexString(c));
        FileOutputStream fos = new FileOutputStream("secreto.txt");
        fos.write(c);
        fos.close();

        //Creo un objeto para hacer MAC del tipo HMACSHA1
        Mac mac = Mac.getInstance("HmacSHA1");

        //Lo inializo con la clave, puedo usar la clave key o encKey indistintamente
        mac.init(key);

        //Le paso el mensaje con el método udpate
        mac.update(m);

        //Finalizo el MAC
        byte[] auth = mac.doFinal();

        //Muestro el MAC por consola
        System.out.println("Authentication " + Util.bytesToHexString(auth));


    }
}