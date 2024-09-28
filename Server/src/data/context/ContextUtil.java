package data.context;

import data.util.Generator;
import entities.TDevice;

import java.security.PrivateKey;
import java.util.Objects;

public class ContextUtil {
    private static final int TDEVICE_ID_LENGTH = 8;

    public static PrivateKey GetPrivateKey(String deviceId){
        for (int i = 0; i < Context.TDevices.size(); i++) {
            if(Objects.equals(Context.TDevices.get(i).devId, deviceId)) {
                return Context.TDevices.get(i).privateKeyFromClient;
            }
        }
        return null;
    }

    public static TDevice findTDevById(String deviceId) {
        for (TDevice device : Context.TDevices) {
            if (Objects.equals(device.devId, deviceId)) {
                return device;
            }
        }
        return null;
    }

    public static String getNewTDeviceId() {
        String newId = Generator.generateRandomString(TDEVICE_ID_LENGTH);
        if (Context.TDevices.stream().anyMatch(d -> d.devId.equals(newId))) {
            return getNewTDeviceId();
        }
        return newId;
    }
}
