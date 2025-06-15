package com.example.g11_group_application.Service_layer;
/**
 * @Author: Divyesh Anuj Srivastava (u7726856)
 * This class is used to test the encryption and decription the password of the user.
 * The password is encrypted using SHA-256 and 3-DES algorithm.
 */

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.SecretKey;

public class PasswordEncryptionTestClass {
    @Test
    public void testGet_SHA_256_SecurePassword() {
        String passwordToHash = "password";
        byte[] salt = new byte[16];
        String generatedPassword = PasswordEncryption.get_SHA_256_SecurePassword(passwordToHash, salt);
        assertEquals("bb5a1bfc6cf736b825e2d7c8874f1ec91d7c0605fdb3cba75db179033b05991c", generatedPassword);
    }

    @Test
    public void testGetSalt() {
        try {
            byte[] salt = PasswordEncryption.getSalt();
            String saltString = Base64.getEncoder().encodeToString(salt);
            System.out.println(saltString);
            assertEquals(16, salt.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testVerifyUserPassword() throws NoSuchAlgorithmException {
        String providedPassword = "password";
        byte[] salt = PasswordEncryption.getSalt();
        String securePassword = PasswordEncryption.get_SHA_256_SecurePassword(providedPassword, salt);
        String saltString = Base64.getEncoder().encodeToString(salt);
        boolean result = PasswordEncryption.verifyUserPassword(providedPassword, securePassword, saltString);
        assertEquals(true, result);
    }

    @Test
    public void testHexStringToByteArray() {
        String s = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
        byte[] data = PasswordEncryption.hexStringToByteArray(s);
        assertEquals(32, data.length);
    }

    @Test
    public void testTripleDESEncodingDecodingKey() {
        String key = "To be or not to be, That is the question";
        String encodedKey = PasswordEncryption.TripleDES_EncodingDecoding_Key(key, 20);
        assertEquals(encodedKey, "Ni vy il hin ni vy, Nbun cm nby koymncih");
    }

    @Test
    public void testTripleDES_encrypt() throws Exception {
        String key = "To be or not to be, That is the question";
        String text = "Hello World!";
        SecretKey secretKey = PasswordEncryption.get3DESKey(key);
        String encryptedText = PasswordEncryption.TripleDES_encrypt(text, secretKey);
        assertEquals(encryptedText, "ako65msZUkHL+ItMyFSlGA==");
    }

    @Test
    public void testTripleDES_decrypt() throws Exception {
        String key = "To be or not to be, That is the question";
        String text = "ako65msZUkHL+ItMyFSlGA==";
        SecretKey secretKey = PasswordEncryption.get3DESKey(key);
        String decryptedText = PasswordEncryption.TripleDES_decrypt(text, secretKey);
        assertEquals(decryptedText, "Hello World!");
    }
}
