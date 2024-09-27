package network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import common.Context;
import common.LogLevel;
import data.Message;
import data.encryption.entities.TDevice;
import data.requests.auth.AuthRequest;

import java.io.IOException;
import java.io.StringWriter;
import java.util.zip.DataFormatException;

import static data.Registration.Register;

public class InnerServer {
    public static void handle(HttpExchange exchange, String json, TDevice device) {
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
                case "Message":
                    Message msg;
                    msg = mapper.treeToValue(dataNode, Message.class);
                    Register(msg);
                case "AuthRequest":
                    AuthRequest request;
                    request = mapper.treeToValue(dataNode, AuthRequest.class);
                    var response = Register(request, device);
                    String result = null;
                    try {
                        result = Cryptography.getJSON(response);
                    } catch (DataFormatException e) {
                        NetUtils.sendDecrypted(DefaultPackages.invalidDataFormat);
                    }
                    try {
                        NetUtils.encryptAndSend(new Package(exchange).decryptedData(result), response.Data);
                    } catch (Exception e) {
                        try {
                            NetUtils.encryptAndSend(new Package(exchange).decryptedData(result), device);
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
