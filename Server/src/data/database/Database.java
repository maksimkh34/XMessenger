package data.database;

import data.util.Generator;
import entities.UserAccount;
import entities.CanDecrypt;

import javax.management.AttributeNotFoundException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Database {
    private static final int MAX_ID_LENGTH = 8;
    private static final int MIN_ID_LENGTH = 4;
    private static final int SECRET_LENGTH = 16;
    private static final String SECRET_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static UserAccount testUser;
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
        return testUser.getHmacKey();
    }

    public static String getNewSecret() {
        var generatedSecret = Generator.generateRandomString(SECRET_LENGTH);
        // if secret exists, return GetNewSecret()
        return generatedSecret;
    }

    public static CanDecrypt userById(String userId) {
        return testUser;
    }

    public boolean tryLoginEmail(String email, String password) {
        return Objects.equals(testUser.Email, email) && Objects.equals(testUser.Password, password);
    }

    public void waitForEmailCode(String email, String code) {
        expectedCodes.put(email, code);
    }

    public boolean codeValid(String email, String code) throws AttributeNotFoundException {
        if(expectedCodes.containsKey(email)) {
            return Objects.equals(expectedCodes.get(email), code);
        }
        throw new AttributeNotFoundException("Email is not in wait list! ");
    }

    public static UserAccount userByEmail(String email) {
        return testUser;
    }

    public static void register(UserAccount userdata) {
        testUser = userdata;
    }
}
