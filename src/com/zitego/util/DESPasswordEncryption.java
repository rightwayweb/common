package com.zitego.util;

import com.zitego.http.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
//import java.security.*;
//import sun.misc.*;

public class DESPasswordEncryption
{
    private Cipher _encryptCipher;
    private Cipher _decryptCipher;
    private static final byte[] IV_BYTES =  { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
    private static final String ENCODING = "ASCII";

    public static void main(String[] args) throws Exception
    {
        DESPasswordEncryption passwordEncryptAgent = new DESPasswordEncryption(new byte[] {0x01, 0x02, 0x04, 0x08, 0x08, 0x04, 0x02, 0x01});
        String encodedEncryptedPassword = passwordEncryptAgent.encrypt(args[0]);
        System.out.println("Encoded encrypted password ..[" + encodedEncryptedPassword + "]");
        String recoveredPassword = passwordEncryptAgent.decrypt(encodedEncryptedPassword);
        System.out.println("Recovered password ..........[" + recoveredPassword + "]");
    }

    /**
     * Creates a new password encryption object with a key. The key must be eight bytes.
     *
     * @param key The key in bytes.
     * @throws Exception if an error occurs.
     */
    public DESPasswordEncryption(byte[] keyBytes) throws Exception
    {
        SecretKey key = new SecretKeySpec(keyBytes, "DES");
        IvParameterSpec iv = new IvParameterSpec(IV_BYTES);
        _encryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding", "SunJCE");
        _encryptCipher.init(Cipher.ENCRYPT_MODE, key, iv); 
        _decryptCipher = Cipher.getInstance("DES/CBC/PKCS5Padding", "SunJCE");
        _decryptCipher.init(Cipher.DECRYPT_MODE, key, iv);
    }

    /**
     * Encrypts a password.
     *
     * @param password The password.
     * @return String
     */
    synchronized public String encrypt(String password) throws Exception
    {
        if (password == null) return null;
        byte[] passwordBytes = password.getBytes(ENCODING);
        byte[] encryptedPasswordBytes = _encryptCipher.doFinal(passwordBytes);
        String encodedEncryptedPassword = Base64.encode(encryptedPasswordBytes);
        return encodedEncryptedPassword;
    }
    
    /**
     * Decrypts a password.
     *
     * @param encryptedPassword The encrypted password.
     * @return String
     */
    synchronized public String decrypt(String encryptedPassword) throws Exception
    {
        if (encryptedPassword == null) return null;
        byte[] encryptedPasswordBytes = Base64.decodeToBytes(encryptedPassword);
        byte[] passwordBytes = _decryptCipher.doFinal(encryptedPasswordBytes);
        String recoveredPassword = new String(passwordBytes, ENCODING);
        return recoveredPassword;
    }
}