package data.database;

import data.context.Context;
import data.logging.LogLevel;
import data.util.Generator;
import entities.UserAccount;
import entities.CanDecrypt;

import javax.management.AttributeNotFoundException;
import java.security.PrivateKey;
import java.util.*;

public class Database {
    private static final int MAX_ID_LENGTH = 8;
    private static final int MIN_ID_LENGTH = 4;
    private static final int SECRET_LENGTH = 16;
    private static final String SECRET_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static List<UserAccount> users = new ArrayList<>();
    public static Map<String, PrivateKey> privateKeyMap = new HashMap<>();

    private final Map<String, String> expectedCodes = new HashMap<>();

    public static String getNewId() {
        Random random = new Random();
        int length = random.nextInt(MAX_ID_LENGTH - MIN_ID_LENGTH + 1) + MIN_ID_LENGTH;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        // if id exists, return GetNewId()
        return sb.toString();
    }

    public static String hmacById(String id) {
        return users.stream().filter(
                userAccount -> Objects.equals(userAccount.Id, id)
        ).findFirst()
                .map(UserAccount::getHmacKey).orElse(null);
    }

    public static String getNewSecret() {
        var generatedSecret = Generator.generateRandomString(SECRET_LENGTH);
        // if secret exists, return GetNewSecret()
        return generatedSecret;
    }

    public static UserAccount userById(String id) {
        var u = users.stream().filter(
                userAccount -> Objects.equals(userAccount.Id, id)
        ).findFirst();
        return u.orElse(null);
    }

    public Boolean tryLoginEmail(String email, String password) {
        var _u = users.stream().filter(
                userAccount -> Objects.equals(userAccount.Email, email)
        ).findFirst();
        if(_u.isEmpty()) return null;
        var u = _u.get();
        return Objects.equals(u.Email, email) && Objects.equals(u.Password, password);
    }

    public void waitForEmailCode(String email, String code) {
        Context.logger.Log(email + " is waiting for code " + code, LogLevel.Info);
        expectedCodes.put(email, code);
    }

    public Boolean codeValid(String email, String code) {
        if(expectedCodes.containsKey(email)) {
            return Objects.equals(expectedCodes.get(email), code);
        }
        return null;
    }

    public static UserAccount userByEmail(String email) {
        var u = users.stream().filter(
                userAccount -> Objects.equals(userAccount.Email, email)
        ).findFirst();
        return u.orElse(null);
    }

    public static void register(UserAccount userdata) {
        users.add(userdata);
    }
}
