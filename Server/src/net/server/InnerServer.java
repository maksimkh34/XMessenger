package net.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import data.context.Context;
import data.logging.LogLevel;
import data.util.Registration;
import entities.CanDecrypt;
import net.NetUtils;
import net.auth.AuthRequest;
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
            throw new RuntimeException(e);
        }
        String type = rootNode.get("type").asText();
        JsonNode dataNode = rootNode.get("data");

        try {
            switch (type) {
                case "AuthRequest":
                    AuthRequest request;
                    request = mapper.treeToValue(dataNode, AuthRequest.class);
                    var response = Registration.HandleAuthRequest(request);
                    String result;
                    result = Json.getJSON(response);
                    NetUtils.encryptAndSend(new Package(exchange).decryptedData(result),
                            response.Data == null
                                    ? receiver      // Если в ответе не указан аккаунт, на который отправляем ответ,
                                                    // отправляем его туда, откуда пришел запрос
                                    : response.Data);
                case "":
            }
        } catch (JsonProcessingException e) {
            NetUtils.encryptAndSend(DefaultPackages.invalidMethod, receiver);
            Context.logger.Log("Got invalid object type", LogLevel.Error);
        }
        NetUtils.sendDecrypted(DefaultPackages.success);
    }
}
