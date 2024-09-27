package data;

import data.encryption.entities.CanDecrypt;
import network.Cryptography;

import java.security.PublicKey;

public class UserAccount extends CanDecrypt {
    public String Login = "Login1";
    public String PUserName = "Slon";       // p - профиль. Данные p_ всего лишь текст в профиле, не больше
    public String PUserSurname = "idk";
    public String Password = "idk";
    public String Email = "idk";
    public String Pk;
    public String Secret;
    public String Id;

    @Override
    public String GetHmacKey() {
        return Id + Secret;
    }

    @Override
    public PublicKey GetPublicKeyToClient() {
        return Cryptography.stringToPublicKey(Pk);
    }

    @Override
    public void SetPublicKeyToClient(PublicKey publicKeyToClient) {
        Pk = Cryptography.publicKeyToString(publicKeyToClient);
    }

    public UserAccount PK(PublicKey PK) {
        var p = this; p.Pk = Cryptography.publicKeyToString(PK); return p;
    }

    public static UserAccount Register(String login, String email, String password) {
        var u = new UserAccount();
        u.Login = login;
        u.Email = email;
        u.Password = password;
        u.Id = Database.GetNewId();
        u.Secret = Database.GetNewSecret();
        var keys = Cryptography.generateKeyPair();
        u.Pk = Cryptography.publicKeyToString(keys.getPublic());
        Database.privateKeyMap.put(u.Id, keys.getPrivate());
        return u;
    }
}

