package net;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.context.Context;
import data.logging.LogLevel;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import data.util.Generator;
import entities.CanDecrypt;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import net.packages.Package;

import static net.cryptography.Json.encryptJson;
import static net.cryptography.HMAC.generateHmac;

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

    public static void encryptAndSend(Package pkg, CanDecrypt acceptor){
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(pkg.decryptedData);
        } catch (JsonProcessingException e) {
            Context.logger.Log("encryptAndSend failed to deserialize decryptedData\n\tclass:\t"
                    + pkg.decryptedData.getClass() + "\n\terr:\t" + e.getMessage(), LogLevel.Error);
            throw new RuntimeException(e);
        }
        String encryptedJson = encryptJson(acceptor.getPublicKeyToClient(), json);
        String hmacSignature = generateHmac(json, acceptor.getHmacKey());

        pkg.exchange.getResponseHeaders().set("Content-Type", "application/json");
        pkg.exchange.getResponseHeaders().set("X-HMAC-Signature", hmacSignature);
        try {
            pkg.exchange.sendResponseHeaders(200, encryptedJson.getBytes(StandardCharsets.UTF_8).length);
        } catch (IOException e) {
            Context.logger.Log("Error sending headers to " + pkg.exchange.getRequestURI() +
                    ": " + e.getMessage(), LogLevel.Error);
            throw new RuntimeException(e);
        }

        try (OutputStream os = pkg.exchange.getResponseBody()) {
            os.write(encryptedJson.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Context.logger.Log("Error sending response to " + pkg.exchange.getRequestURI() +
                    ": " + e.getMessage(), LogLevel.Error);
            throw new RuntimeException(e);
        }
    }

    public static void StartServer(int port, String path, HttpHandler NetHandler) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(path, NetHandler);
        server.setExecutor(null);
        data.context.Context.logger.Log("Server starting...", LogLevel.Info);
        server.start();
    }

    public static String sendAuthCode(String email) {
        var code = Generator.generateRandomString(6);
        // send to email
        return code;
    }
}
