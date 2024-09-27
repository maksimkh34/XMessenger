package network;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.Context;
import common.LogLevel;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import data.CanDecrypt;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static network.Cryptography.encryptJson;
import static network.Cryptography.generateHmac;

public class NetUtils {
    public static void sendDecrypted(Package pkg){
        try {
            pkg.exchange.sendResponseHeaders(pkg.code, pkg.decryptedData.length());
            OutputStream os = pkg.exchange.getResponseBody();
            os.write(pkg.decryptedData.getBytes());
            if(pkg.closeExchange) os.close();
        } catch (IOException e) {
            Context.logger.Log("Error sending response: " + e.getMessage(), LogLevel.Error);
        }
    }

    public static void sendEncrypted(Package pkg, CanDecrypt acceptor) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(pkg.decryptedData);
        String encryptedJson = encryptJson(acceptor.publicKeyToClient, json);
        String hmacSignature = generateHmac(json, acceptor.GetHmacKey());

        pkg.exchange.getResponseHeaders().set("Content-Type", "application/json");
        pkg.exchange.getResponseHeaders().set("X-HMAC-Signature", hmacSignature);
        pkg.exchange.sendResponseHeaders(200, encryptedJson.getBytes(StandardCharsets.UTF_8).length);

        try (OutputStream os = pkg.exchange.getResponseBody()) {
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
