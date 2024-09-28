package net.cryptography;

import data.context.Context;
import data.logging.LogLevel;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeysFactory {
    public static final String KEYS_ALGO = "RSA";
    
    public static KeyPair generateKeyPair() {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance(KEYS_ALGO);
        } catch (NoSuchAlgorithmException e) {
            Context.logger.Log("Invalid algorithm: " + KEYS_ALGO, LogLevel.Error);
            throw new RuntimeException(e);
        }
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    public static PrivateKey stringToPrivateKey(String key){
        byte[] byteKey = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(byteKey);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance(KEYS_ALGO);
        } catch (NoSuchAlgorithmException e) {
            Context.logger.Log("Invalid algorithm: " + KEYS_ALGO, LogLevel.Error);
            throw new RuntimeException(e);
        }
        try {
            return keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            Context.logger.Log("Invalid string PrK: \n\n" + key, LogLevel.Error);
            throw new RuntimeException(e);
        }
    }

    public static String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static PublicKey stringToPublicKey(String key) {
        byte[] byteKey = Base64.getDecoder().decode(key);
        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance(KEYS_ALGO);
        } catch (NoSuchAlgorithmException e) {
            Context.logger.Log("Invalid algorithm: " + KEYS_ALGO, LogLevel.Error);
            throw new RuntimeException(e);
        }
        try {
            return keyFactory.generatePublic(X509publicKey);
        } catch (InvalidKeySpecException e) {
            Context.logger.Log("Invalid string PK: \n\n" + key, LogLevel.Error);
            throw new RuntimeException(e);
        }
    }

    public static String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
}
