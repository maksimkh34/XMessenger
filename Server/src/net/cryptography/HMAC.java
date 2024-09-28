package net.cryptography;

import data.config.Config;
import data.context.Context;
import data.logging.LogLevel;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HMAC {
    private static String HMAC_ALGO = "HmacSHA256";

    public static boolean verifyHMAC(String data, String receivedHmac, String HmacKey) {
        try {
            Mac mac = Mac.getInstance(Config.getValue(Config.HMAC_ALGO));
            SecretKeySpec secretKeySpec = new SecretKeySpec(HmacKey.getBytes(), Config.getValue(Config.HMAC_ALGO));
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes());
            String calculatedHmac = Base64.getEncoder().encodeToString(hmacBytes);
            return calculatedHmac.equals(receivedHmac);
        } catch (Exception e) {
            Context.logger.Log("HMAC verification failed: " + e.getLocalizedMessage(), LogLevel.Error);
            return false;
        }
    }

    public static String generateHmac(String data, String key){
        Mac mac = null;
        try {
            mac = Mac.getInstance(HMAC_ALGO);
        } catch (NoSuchAlgorithmException e) {
            Context.logger.Log("Invalid HMAC_ALGO: " + HMAC_ALGO, LogLevel.Error);
            throw new RuntimeException();
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        try {
            mac.init(secretKeySpec);
        } catch (InvalidKeyException e) {
            Context.logger.Log("Invalid HMAC Key: " + key, LogLevel.Error);
            throw new RuntimeException();
        }
        byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacBytes);
    }
}
