package common;

import data.Database;
import data.encryption.entities.TDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Context {
    public static Logger logger = new Logger(true);
    public static List<TDevice> TDevices = new ArrayList<>();
    public static Database database = new Database();

    public static TDevice findTDevById(String deviceId) {
        for (TDevice device : TDevices) {
            if (Objects.equals(device.DevId, deviceId)) {
                return device;
            }
        }
        return null;
    }
}

