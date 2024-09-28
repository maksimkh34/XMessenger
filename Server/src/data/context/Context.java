package data.context;

import data.logging.Logger;
import data.database.Database;
import entities.TDevice;

import java.util.ArrayList;
import java.util.List;

public class Context {
    public static Logger logger = new Logger(true);
    public static List<TDevice> TDevices = new ArrayList<>();
    public static Database database = new Database();
}

