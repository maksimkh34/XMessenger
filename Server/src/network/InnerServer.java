package network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import common.Context;
import common.LogLevel;
import data.Registration;
import data.encryption.entities.CanDecrypt;
import data.requests.auth.AuthRequest;

import java.util.zip.DataFormatException;

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
                    String result = null;
                    try {
                        result = Cryptography.getJSON(response);
                    } catch (DataFormatException e) {
                        NetUtils.sendDecrypted(DefaultPackages.invalidDataFormat);
                    }
                    try {
                        NetUtils.encryptAndSend(new Package(exchange).decryptedData(result),
                                response.Data == null ? receiver : response.Data);
                    } catch (Exception e) {
                        try {
                            NetUtils.encryptAndSend(new Package(exchange).decryptedData(result),
                                    response.Data == null ? receiver : response.Data);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                case "":
            }
        } catch (JsonProcessingException e) {
            NetUtils.sendDecrypted(DefaultPackages.invalidMethod);
            Context.logger.Log("Got invalid object type", LogLevel.Error);
        }
        NetUtils.sendDecrypted(DefaultPackages.success);
    }
}
