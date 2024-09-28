package net.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import data.context.Context;
import data.context.ContextUtil;
import data.logging.LogLevel;
import data.util.Registration;
import entities.CanDecrypt;
import entities.TDevice;
import net.NetUtils;
import net.cryptography.KeysFactory;
import net.requests.auth.AuthRequest;
import net.cryptography.Json;
import net.packages.DefaultPackages;
import net.packages.Package;

public class InnerServer {
    public static void handle(HttpExchange exchange, String json, CanDecrypt receiver) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JsonNode rootNode;
        try {
            rootNode = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            Context.logger.Log("Error reading tree: \n\n" + json, LogLevel.Error);
            throw new RuntimeException(e);
        }
        String type = rootNode.get("type").asText();
        JsonNode dataNode = rootNode.get("data");

            switch (type) {
                case "AuthRequest":
                    Context.logger.Log("Got new AuthRequest!", LogLevel.Info);
                    AuthRequest request;
                    try {
                        request = mapper.treeToValue(dataNode, AuthRequest.class);
                    } catch (JsonProcessingException e) {
                        Context.logger.Log("Error processing AuthRequest: " + dataNode, LogLevel.Error);
                        throw new RuntimeException();
                    }
                    try {
                        if (request.PermServerToClient != null && receiver.getClass() == TDevice.class)
                            ContextUtil.findTDevById(((TDevice) receiver).devId)
                                    .setPublicKeyToClient(KeysFactory.stringToPublicKey(request.PermServerToClient));
                    } catch (NullPointerException ignored) { }
                    var response = Registration.HandleAuthRequest(request);
                    Context.logger.Log("Response " + response.Result, LogLevel.Info);
                    String result = Json.getJSON(response);
                    NetUtils.encryptAndSend(new Package(exchange).decryptedData(result),
                            response.Data == null
                                    ? receiver      // Если в ответе не указан аккаунт, на который отправляем ответ,
                                                    // отправляем его туда, откуда пришел запрос
                                    : response.Data);
                    return;
                default:
                    Context.logger.Log("Invalid type: " + type, LogLevel.Error);
            }
        NetUtils.sendDecrypted(DefaultPackages.success);
    }
}
