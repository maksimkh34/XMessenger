package common;

import data.encryption.entities.TDevice;

import javax.management.AttributeNotFoundException;
import java.security.PrivateKey;
import java.util.Objects;

public class ContextUtil {
    public static PrivateKey GetPrivateKey(String deviceId) throws AttributeNotFoundException {
        for (int i = 0; i < Context.TDevices.size(); i++) {
            if(Objects.equals(Context.TDevices.get(i).DevId, deviceId)) {
                return Context.TDevices.get(i).privateKeyFromClient;
            }
        }
        throw new AttributeNotFoundException("No device with provided ID found");
    }

    public static TDevice findTDevById(String deviceId) {
        for (TDevice device : Context.TDevices) {
            if (Objects.equals(device.DevId, deviceId)) {
                return device;
            }
        }
        return null;
    }
}
