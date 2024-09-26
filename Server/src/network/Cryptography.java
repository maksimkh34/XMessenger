package network;

import common.Config;
import common.Context;
import common.LogLevel;
import org.jose4j.jwe.JsonWebEncryption;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class Cryptography {
    public static KeyPair keys = Cryptography.generateKeyPair();;

    public static String decryptJson(String encryptedJson, String privateKeyStr) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        JsonWebEncryption jwe = new JsonWebEncryption();
        jwe.setCompactSerialization(encryptedJson);
        jwe.setKey(privateKey);

        return jwe.getPayload();
    }

    public static KeyPair generateKeyPair() {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    public static String GetPublicKey() {
        return Base64.getEncoder().encodeToString(keys.getPublic().getEncoded());
    }

    public static String GetPrivateKey() {
        return Base64.getEncoder().encodeToString(keys.getPrivate().getEncoded());
    }

    public static boolean verifyHMAC(String data, String receivedHmac) {
        try {
            Mac mac = Mac.getInstance(Config.getValue(Config.HMAC_ALGO));
            SecretKeySpec secretKeySpec = new SecretKeySpec(Config.getValue(Config.HMAC_KEY).getBytes(), Config.getValue(Config.HMAC_ALGO));
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes());
            String calculatedHmac = Base64.getEncoder().encodeToString(hmacBytes);
            return calculatedHmac.equals(receivedHmac);
        } catch (Exception e) {
            Context.logger.Log("HMAC verification failed", LogLevel.Error);
            return false;
        }
    }
}
