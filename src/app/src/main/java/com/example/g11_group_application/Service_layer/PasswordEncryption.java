package com.example.g11_group_application.Service_layer;
/**
 * @Author: Divyesh Anuj Srivastava (u7726856)
 * This class is used to encrypt the password of the user.
 * The password is encrypted using SHA-256 and 3-DES algorithm.
 */

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class PasswordEncryption {
    public static String get_SHA_256_SecurePassword(String passwordToHash, byte[] salt) {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
    // Method to generate a salt
    public static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstanceStrong();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    public static boolean verifyUserPassword(String providedPassword, String securedPassword, String salt) {
        try {
            // Convert the salt back to byte array from its stored string representation
            byte[] saltBytes = Base64.getDecoder().decode(salt);

            // Hash the provided password with the same salt
            String newSecurePassword = get_SHA_256_SecurePassword(providedPassword, saltBytes);

            // Check if the new hash matches the stored hash
            return newSecurePassword.equalsIgnoreCase(securedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static String encryptPassword(String password) {
        try {
            byte[] salt = getSalt(); // Generate the salt
            String saltString = Base64.getEncoder().encodeToString(salt);
            String passwordHash = get_SHA_256_SecurePassword(password, salt);
            return passwordHash.substring(0, 32) + "@" + saltString + passwordHash.substring(32);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static boolean verifyPassword(String providedPassword, String securedPassword) throws NoSuchAlgorithmException {
        try {
            int atIndex = securedPassword.indexOf('@');
            String hashPart1 = securedPassword.substring(0, atIndex);
            String saltString = securedPassword.substring(atIndex + 1, atIndex + 25);
            String hashPart2 = securedPassword.substring(atIndex + 25);
            String fullHash = hashPart1 + hashPart2;
            return verifyUserPassword(providedPassword, fullHash, saltString);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3-DES
    public static SecretKey get3DESKey(String baseKey) {
        byte[] keyBytes = Arrays.copyOf(baseKey.getBytes(StandardCharsets.UTF_8), 24);
        return new SecretKeySpec(keyBytes, "DESede");
    }

    public static String TripleDES_encrypt(String plaintext, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String TripleDES_decrypt(String ciphertext, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static String TripleDES_EncodingDecoding_Key(String PlainText, int ShiftIndicator){
        byte[] EncodingAscii = PlainText.getBytes(StandardCharsets.US_ASCII);
        byte[] EncodingAsciiClone = EncodingAscii.clone();
        String EncodedText = "";

        for (int j=0;j<EncodingAscii.length;j++){
            int IntEncodingAscii = 0;

            if ((EncodingAscii[j] >= 65 && EncodingAscii[j] <= 90)
                    || (EncodingAscii[j] >= 97 && EncodingAscii[j] <= 122)){
                IntEncodingAscii = (int) (EncodingAscii[j] + ShiftIndicator);
            }
            else {
                IntEncodingAscii = (int) (EncodingAscii[j]);
            }

            if (EncodingAscii[j] >= 65 && EncodingAscii[j] <= 90){
                if (ShiftIndicator > 0){
                    if (IntEncodingAscii > 90) {
                        IntEncodingAscii = (byte) (65 + (IntEncodingAscii - 91));
                    }
                }

                else {
                    if (IntEncodingAscii < 65) {
                        IntEncodingAscii = (byte) (91 - (65 - IntEncodingAscii));
                    }
                }
            }

            if (EncodingAscii[j] >= 97 && EncodingAscii[j] <= 122){
                if (ShiftIndicator > 0){
                    if (IntEncodingAscii > 122) {
                        IntEncodingAscii = (byte) (97 + (IntEncodingAscii - 123));
                    }
                }

                else {
                    if (IntEncodingAscii < 97) {
                        IntEncodingAscii = (byte) (123 - (97 - IntEncodingAscii));
                    }
                }
            }
            EncodingAsciiClone[j] = (byte) IntEncodingAscii;
        }

        for (int i = 0; i<EncodingAsciiClone.length;i++){
            char cc = (char) EncodingAsciiClone[i];
            String sc = String.valueOf(cc);
            EncodedText = EncodedText + sc;
        }
        return EncodedText;
    }
}
