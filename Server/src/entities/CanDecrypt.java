package entities;

import data.config.Config;

import java.security.PublicKey;

public class CanDecrypt {
    private final String defaultHmacKey = Config.getValue(Config.HMAC_KEY);
    private PublicKey publicKeyToClient;

    public String getHmacKey() {
        return defaultHmacKey;
    }

    public PublicKey getPublicKeyToClient() {
        return publicKeyToClient;
    }

    public void setPublicKeyToClient(PublicKey publicKeyToClient) {
        this.publicKeyToClient = publicKeyToClient;
    }
}
