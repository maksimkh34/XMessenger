package network;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.Context;
import common.LogLevel;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;

import static network.Cryptography.encryptJson;
import static network.Cryptography.generateHmac;

public class NetUtils {
    public static void sendDecrypted(HttpExchange exchange, int code, String message, Boolean close){
        try {
            exchange.sendResponseHeaders(code, message.length());
            OutputStream os = exchange.getResponseBody();
            os.write(message.getBytes());
            if(close) os.close();
        } catch (IOException e) {
            Context.logger.Log("Error sending response: " + e.getMessage(), LogLevel.Error);
        }
    }

    public static void sendEncrypted(Object obj, HttpExchange exchange, PublicKey publicKey, String hmacKey) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(obj);
        String encryptedJson = encryptJson(publicKey, json);
        String hmacSignature = generateHmac(json, hmacKey);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("X-HMAC-Signature", hmacSignature);
        exchange.sendResponseHeaders(200, encryptedJson.getBytes(StandardCharsets.UTF_8).length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(encryptedJson.getBytes(StandardCharsets.UTF_8));
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
