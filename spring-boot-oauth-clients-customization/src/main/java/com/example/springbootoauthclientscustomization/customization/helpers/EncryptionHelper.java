package com.example.springbootoauthclientscustomization.customization.helpers;

import com.example.springbootoauthclientscustomization.customization.CustomSavedRequestAwareAuthenticationSuccessHandler;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.SerializationUtils;

public class EncryptionHelper {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int KEY_ITERATION_COUNT = 100_000; // https://security.stackexchange.com/q/3959
    private static final int KEY_SIZE = 32;
    private static final int IV_SIZE = 16;

    public static final Base64.Encoder B64E = Base64.getEncoder();
    public static final Base64.Decoder B64D = Base64.getDecoder();

     public static final SecretKey encryptionKey = EncryptionHelper.generateKey(); // we can also use generateKey(secret, salt) method.

    @SneakyThrows
    public static SecretKey generateKey() {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        int keySizeBits = KEY_SIZE * 8;
        generator.init(keySizeBits, RANDOM);
        return generator.generateKey();
    }

    @SneakyThrows
    public static SecretKey generateKey(@NonNull char[] password, @NonNull byte[] salt) {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        int keySizeBits = KEY_SIZE * 8;
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, KEY_ITERATION_COUNT, keySizeBits);
        SecretKey temporaryKey = factory.generateSecret(keySpec);
        keySpec.clearPassword();
        return new SecretKeySpec(temporaryKey.getEncoded(), "AES");
    }

    @SneakyThrows
    public static byte[] encrypt(@NonNull SecretKey key, @NonNull byte[] clearText) {
        byte[] ivBytes = new byte[IV_SIZE];
        RANDOM.nextBytes(ivBytes);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(ivBytes));
        byte[] cipherBytes = cipher.doFinal(clearText);
        return concat(ivBytes, cipherBytes);
    }

    @SneakyThrows
    public static byte[] decrypt(@NonNull SecretKey key, @NonNull byte[] cipherText) {
        byte[][] byteArrays = split(cipherText);
        byte[] ivBytes = byteArrays[0];
        byte[] cipherBytes = byteArrays[1];
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivBytes));
        return cipher.doFinal(cipherBytes);
    }

    private static byte[] concat(byte[] ivBytes, byte[] cipherBytes) {
        byte[] concatenatedBytes = new byte[ivBytes.length + cipherBytes.length];
        System.arraycopy(ivBytes, 0, concatenatedBytes, 0, ivBytes.length);
        System.arraycopy(cipherBytes, 0, concatenatedBytes, ivBytes.length, cipherBytes.length);
        return concatenatedBytes;
    }

    private static byte[][] split(byte[] concatenatedBytes) {
        byte[] ivBytes = new byte[IV_SIZE];
        byte[] cipherBytes = new byte[concatenatedBytes.length - IV_SIZE];
        System.arraycopy(concatenatedBytes, 0, ivBytes, 0, IV_SIZE);
        System.arraycopy(concatenatedBytes, IV_SIZE, cipherBytes, 0, concatenatedBytes.length - IV_SIZE);
        return new byte[][]{ivBytes, cipherBytes};
    }

    public static String encrypt(OAuth2AuthorizationRequest authorizationRequest) {
        byte[] bytes = SerializationUtils.serialize(authorizationRequest);
        byte[] encryptedBytes = EncryptionHelper.encrypt(encryptionKey, bytes);
        return B64E.encodeToString(encryptedBytes);
    }

    public static String encrypt(CustomSavedRequestAwareAuthenticationSuccessHandler.AuthenticationDataToStore authentication) {
        byte[] bytes = SerializationUtils.serialize(authentication);
        byte[] encryptedBytes = EncryptionHelper.encrypt(encryptionKey, bytes);
        return B64E.encodeToString(encryptedBytes);
    }

    public static Object decrypt(String encrypted) {
        byte[] encryptedBytes = B64D.decode(encrypted);
        byte[] bytes = EncryptionHelper.decrypt(encryptionKey, encryptedBytes);
        return SerializationUtils.deserialize(bytes);
    }

}
