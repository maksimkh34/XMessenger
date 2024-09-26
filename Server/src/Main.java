import java.io.IOException;

import common.Config;
import common.Context;
import common.LogLevel;
import network.NetUtils;
import network.OuterServer;

public class Main {

    public static void main(String[] args) throws IOException {
        Config.load();
        Context.logger.Log("Server version: " + Config.getValue(Config.SERVER_VERSION), LogLevel.Info);
        NetUtils.StartServer(Integer.parseInt(Config.getValue(Config.SERVER_PORT)),
                Config.getValue(Config.SERVER_PATH),
                new OuterServer.NetHandler());
        Context.logger.Log("Started.", LogLevel.Info);
    }
}
