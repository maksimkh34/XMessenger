package net.cryptography;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import data.context.Context;
import data.logging.LogLevel;

import java.io.IOException;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.zip.DataFormatException;

public class Json {
    public static String decryptJson(PrivateKey privateKey, String encryptedJson) {
        EncryptedJWT jwt = null;
        try {
            jwt = EncryptedJWT.parse(encryptedJson);
        } catch (ParseException e) {
            Context.logger.Log("Error parsing JWT. Full json: \n\n" + encryptedJson + "\n\n",
                    LogLevel.Error);
            throw new RuntimeException(e);
        }

        RSADecrypter decrypter = new RSADecrypter(privateKey);
        try {
            jwt.decrypt(decrypter);
        } catch (JOSEException e) {
            Context.logger.Log("Error decrypting json. Full json: \n\n" + encryptedJson + "\n\n",
                    LogLevel.Error);
            throw new RuntimeException(e);
        }

        try {
            return jwt.getJWTClaimsSet().getStringClaim("data");
        } catch (ParseException e) {
            try {
                return jwt.getJWTClaimsSet().toString();
            } catch (ParseException ex) {
                Context.logger.Log("Error getting JSON claims. JSON: \n\n" + encryptedJson + "\n\n",
                        LogLevel.Error);
                throw new RuntimeException(ex);
            }
        }
    }

    public static String getJSON(Object obj){
        ObjectMapper jsonMapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        try {
            jsonMapper.writeValue(writer, obj);
        } catch (IOException e) {
            Context.logger.Log("Error deserializing " + obj.getClass() + ": " + e.getMessage(),
                    LogLevel.Error);
            throw new RuntimeException("Error serializing obj");
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
            Context.logger.Log("Error encrypting json. Full json: \n\n" + json
                            + "\n\nProvided PK:\n\n" + KeysFactory.publicKeyToString(publicKey), LogLevel.Error);
            throw new RuntimeException(e);
        }
        return jwt.serialize();
    }
}
