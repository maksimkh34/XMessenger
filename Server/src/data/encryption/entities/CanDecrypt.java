package data.encryption.entities;

import common.Config;

import java.security.PublicKey;

public class CanDecrypt {
    // Default HMAC Key to sign packages. In UsrAccount hmac key will not be default
    private final String defaultHmacKey = Config.getValue(Config.HMAC_KEY);
    // Public key, should be used to decrypt data that will be sent to Client
    private PublicKey publicKeyToClient;

    public String GetHmacKey() {
        return defaultHmacKey;
    }

    public PublicKey GetPublicKeyToClient() {
        return publicKeyToClient;
    }

    public void SetPublicKeyToClient(PublicKey publicKeyToClient) {
        this.publicKeyToClient = publicKeyToClient;
    }
}
