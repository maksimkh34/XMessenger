package entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.cryptography.KeysFactory;
import data.database.Database;

import java.security.PrivateKey;
import java.security.PublicKey;

public class UserAccount extends CanDecrypt {
    public String Login = "Login1";
    public String PUserName = "Slon";       // p - профиль. Данные p_ всего лишь текст в профиле, не больше
    public String PUserSurname = "idk";
    public String Password = "idk";
    public String Email = "idk";
    public String Pk;       // Публичный ключ STC
    public String Secret;
    public String Id;

    @Override
    public String getHmacKey() {
        return Id + Secret;
    }

    @JsonIgnore
    @Override
    public PublicKey getPublicKeyToClient() {
        return KeysFactory.stringToPublicKey(Pk);
    }

    @JsonIgnore
    @Override
    public void setPublicKeyToClient(PublicKey publicKeyToClient) {
        Pk = KeysFactory.publicKeyToString(publicKeyToClient);
    }

    public UserAccount PK(PublicKey PK) {
        var p = this; p.Pk = KeysFactory.publicKeyToString(PK); return p;
    }

    public static UserAccount register(String login, String email, String password, String stc, PrivateKey ctsPrivate) {
        var u = new UserAccount();
        u.Login = login;
        u.Email = email;
        u.Password = password;
        u.Id = Database.getNewId();
        u.Secret = Database.getNewSecret();
        u.Pk = stc;
        Database.privateKeyMap.put(u.Id, ctsPrivate);
        return u;
    }
}

