package com.example.Problem2;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class EncryptPassword {

    /**
     * Method to encrypt password
     *
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String encryptPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] hashPassword = md.digest();
        return bytesToHexaDecimal(hashPassword);
    }

    /**
     * Method to convert bytes to hexadecimal
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexaDecimal(byte[] bytes) {
        StringBuilder hexaString = new StringBuilder();
        for (byte b : bytes) {
            String hexa = Integer.toHexString(0xff & b);
            if (hexa.length() == 1) hexaString.append('0');
            hexaString.append(hexa);
        }
        return hexaString.toString();
    }
}





