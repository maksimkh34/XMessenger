package data;

import data.encryption.entities.CanDecrypt;

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

    public static String GetNewId() {
        Random random = new Random();
        int length = random.nextInt(MAX_ID_LENGTH - MIN_ID_LENGTH + 1) + MIN_ID_LENGTH;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // Генерирует случайную цифру от 0 до 9
        }
        // if id exists, return GetNewId()
        return sb.toString();
    }

    public static String HmacById(String id) {
        return testUser.GetHmacKey();
    }

    public static String GetNewSecret() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(SECRET_LENGTH);
        for (int i = 0; i < SECRET_LENGTH; i++) {
            int index = random.nextInt(SECRET_CHARACTERS.length());
            sb.append(SECRET_CHARACTERS.charAt(index));
        }
        // if secret exists, return GetNewSecret()
        return sb.toString();
    }

    public static CanDecrypt UserById(String userId) {
        return testUser;
    }

    public boolean TryLoginEmail(String email, String password) {
        return Objects.equals(testUser.Email, email) && Objects.equals(testUser.Password, password);
    }

    public Database() { }

    public void WaitForEmailCode(String email, String code) {
        expectedCodes.put(email, code);
    }

    public boolean codeValid(String email, String code) throws AttributeNotFoundException {
        if(expectedCodes.containsKey(email)) {
            return Objects.equals(expectedCodes.get(email), code);
        }
        throw new AttributeNotFoundException("Email is not in wait list! ");
    }

    public static UserAccount UserByEmail(String email) {
        return testUser;
    }

    public static void Register(UserAccount userdata) {
        testUser = userdata;
    }
}
