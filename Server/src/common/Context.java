package common;

import data.Database;
import data.encryption.entities.TDevice;

import java.util.ArrayList;
import java.util.List;

public class Context {
    public static Logger logger = new Logger(true);
    public static List<TDevice> TDevices = new ArrayList<>();
    public static Database database = new Database();
}

