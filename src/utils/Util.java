package utils;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

    public static String bytesToString(byte[] b) {
        try {
            return new String(b, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static byte[] stringToBytes(String s) {
        return s.getBytes();
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    public static byte cambiarBit(byte b, int i){
        return (byte) (b ^ ((0x80) >>> i));
    }


    public static byte[] xor(byte[] x, byte[] y){
        int min = Math.min(x.length, y.length);
        byte[] z = new byte[min];
        for(int i=0; i<min; i++){
            z[i] = (byte) (x[i] ^ y[i]);
        }
        return z;
    }


    public static byte[] mensajeAleatorio(int n) {
        byte[] r = new byte[n];
        (new SecureRandom()).nextBytes(r);
        return r;
    }

    public static int numeroAleatorio(int n) {
        return (new SecureRandom()).nextInt(n);
    }

    public static int contarBitsDiferentes(byte b, byte c){
        return Integer.bitCount((b ^ c) & 0xFF);
    }



}