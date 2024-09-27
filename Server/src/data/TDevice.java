package data;

import java.security.PrivateKey;
import java.security.PublicKey;

public class TDevice extends CanDecrypt{
    // Private key that should be used to decrypt data received from Client
    public PrivateKey privateKeyFromClient;
    // Unique device id 
    public String DevId;
}
