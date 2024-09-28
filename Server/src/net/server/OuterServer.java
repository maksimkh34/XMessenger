package net.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import data.config.Config;
import data.context.Context;
import data.context.ContextUtil;
import data.database.Database;
import data.logging.LogLevel;
import entities.TDevice;
import net.NetUtils;
import net.cryptography.KeysFactory;
import net.packages.DefaultPackages;
import net.packages.Package;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Objects;

public class OuterServer {
    public static class NetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();

            if ("POST".equals(exchange.getRequestMethod())) {
                Context.logger.Log("Got new POST from " + exchange.getRemoteAddress(), LogLevel.Info);
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                String receivedHmac = exchange.getRequestHeaders().getFirst("X-HMAC-Signature");
                var hmacKey = Config.getValue(Config.HMAC_KEY);

                if(exchange.getRequestHeaders().containsKey("DeviceId")) {
                    hmacKey = exchange.getRequestHeaders().getFirst("DeviceId");
                }

                if(exchange.getRequestHeaders().containsKey("UserId")) {
                    var usrId = exchange.getRequestHeaders().getFirst("UserId");
                    hmacKey = Database.hmacById(usrId);
                }

                Context.logger.Log("Selected HMAC key: " + hmacKey +
                        (Objects.equals(hmacKey, Config.getValue(Config.HMAC_KEY)) ? " (Default)" : ""), LogLevel.Info);

                if (!net.cryptography.HMAC.verifyHMAC(json, receivedHmac, hmacKey)) {
                    Context.logger.Log("Invalid HMAC signature. Key: " + hmacKey, LogLevel.Error);
                    NetUtils.encryptAndSend(DefaultPackages.unauthorized,
                            Objects.requireNonNull(
                                    ContextUtil.findTDevById(
                                            exchange.getRequestHeaders().getFirst("DeviceId"))));
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
                        Context.logger.Log("Got new PK request! ", LogLevel.Info);
                        var t = new TDevice();
                        var tempKeyPair = KeysFactory.generateKeyPair();

                        t.privateKeyFromClient = tempKeyPair.getPrivate();
                        t.devId = ContextUtil.getNewTDeviceId();
                        t.setPublicKeyToClient(
                                KeysFactory.stringToPublicKey(data.replace("TPKeyRequest:", ""))
                        );
                        Context.TDevices.add(t);

                        Package pkg = new Package(exchange,
                                200,
                                t.devId + KeysFactory.publicKeyToString(tempKeyPair.getPublic()),
                                true);

                        try {
                            NetUtils.encryptAndSend(pkg, t);
                            Context.logger.Log("Sent public key to device " + t.devId + "!", LogLevel.Info);
                        } catch (Exception e) {
                            Context.logger.Log("Error sending TPKResponse", LogLevel.Error);
                        }
                    } else {
                        Context.logger.Log("Invalid data format" + exchange.getRequestMethod(), LogLevel.Error);
                        NetUtils.sendDecrypted(DefaultPackages.invalidDataFormat.exchange(exchange));
                    }
                } catch (JsonProcessingException e) {
                    // Got encrypted msg from device
                    if(exchange.getRequestHeaders().containsKey("DeviceId")) {
                        var deviceId = exchange.getRequestHeaders().getFirst("DeviceId");
                        Context.logger.Log("Got new pkg from " + deviceId, LogLevel.Info);
                        PrivateKey privateKey;
                        privateKey = ContextUtil.GetPrivateKey(deviceId);
                        var decrypted = net.cryptography.Json.decryptJson(privateKey, json);
                        InnerServer.handle(exchange, decrypted, ContextUtil.findTDevById(deviceId));
                    }

                    // Got encrypted msg from user
                    else if(exchange.getRequestHeaders().containsKey("UserId")) {
                        var userId = exchange.getRequestHeaders().getFirst("UserId");
                        Context.logger.Log("Got new pkg from " + userId, LogLevel.Info);
                        PrivateKey privateKey;
                        privateKey = Database.privateKeyMap.get(userId);
                        var decrypted = net.cryptography.Json.decryptJson(privateKey, json);
                        InnerServer.handle(exchange, decrypted, Database.userById(userId));
                    }

                }

            } else {
                Context.logger.Log("Expected POST, got " + exchange.getRequestMethod(), LogLevel.Error);
                NetUtils.sendDecrypted(DefaultPackages.invalidMethod.exchange(exchange));
            }
        }

    }
}
