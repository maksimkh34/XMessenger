package network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import common.Context;
import common.LogLevel;
import data.CanDecrypt;
import data.Generator;
import data.TDevice;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class OuterServer {
    public static class NetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Context.logger.Log("Got HttpExchange!", LogLevel.Info);
            InputStream is = exchange.getRequestBody();
            if ("POST".equals(exchange.getRequestMethod())) {
                Context.logger.Log("Type: POST", LogLevel.Info);
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                String receivedHmac = exchange.getRequestHeaders().getFirst("X-HMAC-Signature");
                if (!Cryptography.verifyHMAC(json, receivedHmac)) {
                    NetUtils.sendDecrypted(DefaultPackages.unauthorized);
                    Context.logger.Log("Invalid HMAC signature", LogLevel.Error);
                    return;
                }
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                JsonNode rootNode;
                try {
                    rootNode = mapper.readTree(json);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                String data = rootNode.get("data").asText();
                if (data.startsWith("TPKeyRequest:")) {
                    Context.logger.Log("Got public key request", LogLevel.Info);
                    var t = new TDevice();
                    var keyPair = Cryptography.generateKeyPair();
                    t.privateKeyFromClient = keyPair.getPrivate();
                    t.DevId = Generator.getNewTDeviceId();
                    t.publicKeyToClient = Cryptography.stringToPublicKey(data.replace("TPKeyRequest:", ""));
                    Context.TDevices.add(t);

                    Package pkg = new Package(exchange,
                            200,
                            t.DevId + Cryptography.publicKeyToString(keyPair.getPublic()),
                            true);
                    try {
                        NetUtils.sendEncrypted(pkg, t);
                        Context.logger.Log("Sent public key to device " + t.DevId + "!", LogLevel.Info);
                    } catch (Exception e) {
                        Context.logger.Log("Error sending TPKResponse", LogLevel.Error);
                    }
                    return;
                }
                InnerServer.handle(exchange, json);
            } else {
                Context.logger.Log("Expected POST, got " + exchange.getRequestMethod(), LogLevel.Warning);
                NetUtils.sendDecrypted(DefaultPackages.invalidMethod.exchange(exchange));
            }
        }

    }
}
