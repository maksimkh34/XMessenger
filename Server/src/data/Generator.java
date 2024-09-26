package data;

import common.Context;

import java.security.SecureRandom;
import java.util.Objects;

public class Generator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int TDEVICE_ID_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
    public static String getNewTDeviceId() {
        String newId = generateRandomString(TDEVICE_ID_LENGTH);
        if (Context.TDevices.stream().anyMatch(d -> d.DevId.equals(newId))) {
            return getNewTDeviceId();
        }
        return newId;
    }
}
