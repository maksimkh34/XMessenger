package entities;

import java.security.PrivateKey;

public class TDevice extends CanDecrypt {
    // Private key that should be used to decrypt data received from Client
    public PrivateKey privateKeyFromClient;
    // Unique device id
    public String devId;
}
