package network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import common.Config;
import common.Context;
import common.ContextUtil;
import common.LogLevel;
import data.Generator;
import data.encryption.entities.TDevice;

import javax.management.AttributeNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

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
                var hmacKey = Config.getValue(Config.HMAC_KEY);
                if(exchange.getRequestHeaders().containsKey("DeviceId")) {
                    hmacKey = exchange.getRequestHeaders().getFirst("DeviceId");
                }
                if (!Cryptography.verifyHMAC(json, receivedHmac, hmacKey)) {
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
                    String data = rootNode.get("data").asText();
                    if (data.startsWith("TPKeyRequest:")) {
                        Context.logger.Log("Got public key request", LogLevel.Info);
                        var t = new TDevice();
                        var keyPair = Cryptography.generateKeyPair();
                        t.privateKeyFromClient = keyPair.getPrivate();
                        t.DevId = Generator.getNewTDeviceId();
                        t.SetPublicKeyToClient(
                                Cryptography.stringToPublicKey(data.replace("TPKeyRequest:", ""))
                        );
                        Context.TDevices.add(t);

                        Package pkg = new Package(exchange,
                                200,
                                t.DevId + Cryptography.publicKeyToString(keyPair.getPublic()),
                                true);
                        try {
                            NetUtils.encryptAndSend(pkg, t);
                            Context.logger.Log("Sent public key to device " + t.DevId + "!", LogLevel.Info);
                        } catch (Exception e) {
                            Context.logger.Log("Error sending TPKResponse", LogLevel.Error);
                        }
                    }
                } catch (JsonProcessingException e) {
                    if(exchange.getRequestHeaders().containsKey("DeviceId")) {
                        var deviceId = exchange.getRequestHeaders().getFirst("DeviceId");
                        PrivateKey privateKey;
                        try {
                            privateKey = ContextUtil.GetPrivateKey(deviceId);
                        } catch (AttributeNotFoundException ex) {
                            NetUtils.sendDecrypted(DefaultPackages.invalidTDeviceID);
                            return;
                        }
                        var decrypted = Cryptography.decryptJson(privateKey, json);
                        InnerServer.handle(exchange, decrypted, Context.findTDevById(deviceId));
                    }
                }
            } else {
                Context.logger.Log("Expected POST, got " + exchange.getRequestMethod(), LogLevel.Warning);
                NetUtils.sendDecrypted(DefaultPackages.invalidMethod.exchange(exchange));
            }
        }

    }
}
