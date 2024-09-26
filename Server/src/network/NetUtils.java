package network;

import common.Context;
import common.LogLevel;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class NetUtils {
    public static void SendResponse(HttpExchange exchange, int code, String message, Boolean close){
        try {
            exchange.sendResponseHeaders(code, message.length());
            OutputStream os = exchange.getResponseBody();
            os.write(message.getBytes());
            if(close) os.close();
        } catch (IOException e) {
            Context.logger.Log("Error sending response: " + e.getMessage(), LogLevel.Error);
        }
    }

    public static void StartServer(int port, String path, HttpHandler NetHandler) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(path, NetHandler);
        server.setExecutor(null);
        common.Context.logger.Log("Server starting...", LogLevel.Info);
        server.start();
    }
}
