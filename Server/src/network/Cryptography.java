package network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import common.Config;
import common.Context;
import common.LogLevel;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Objects;
import java.util.zip.DataFormatException;

public class Cryptography {

    public static String decryptJson(PrivateKey privateKey, String encryptedJson) {
        EncryptedJWT jwt = null;
        try {
            jwt = EncryptedJWT.parse(encryptedJson);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        RSADecrypter decrypter = new RSADecrypter(privateKey);
        try {
            jwt.decrypt(decrypter);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        try {
            return jwt.getJWTClaimsSet().getStringClaim("data");
        } catch (ParseException e) {
            try {
                return jwt.getJWTClaimsSet().toString();
            } catch (ParseException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static String getJSON(Object obj) throws DataFormatException {
        ObjectMapper jsonMapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            jsonMapper.writeValue(writer, obj);
        } catch (IOException e) {
            throw new DataFormatException("Error serializing obj");
        }
        return writer.toString();
    }

    public static String encryptJson(PublicKey publicKey, String json) {
        JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256GCM);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().claim("data", json).build();
        EncryptedJWT jwt = new EncryptedJWT(header, claimsSet);

        RSAEncrypter encrypter = new RSAEncrypter((RSAPublicKey) publicKey);
        try {
            jwt.encrypt(encrypter);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        return jwt.serialize();
    }

    public static boolean verifyHMAC(String data, String receivedHmac, String HmacKey) {
        try {
            Mac mac = Mac.getInstance(Config.getValue(Config.HMAC_ALGO));
            SecretKeySpec secretKeySpec = new SecretKeySpec(HmacKey.getBytes(), Config.getValue(Config.HMAC_ALGO));
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes());
            String calculatedHmac = Base64.getEncoder().encodeToString(hmacBytes);
            return calculatedHmac.equals(receivedHmac);
        } catch (Exception e) {
            Context.logger.Log("HMAC verification failed", LogLevel.Error);
            return false;
        }
    }

    public static String generateHmac(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    public static KeyPair generateKeyPair() {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    public static PrivateKey stringToPrivateKey(String key) throws Exception {
        byte[] byteKey = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(byteKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static PublicKey stringToPublicKey(String key) {
        byte[] byteKey = Base64.getDecoder().decode(key);
        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        try {
            return keyFactory.generatePublic(X509publicKey);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }
}
