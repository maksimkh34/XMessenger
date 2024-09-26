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

import java.io.IOException;

import static data.Registration.Register;

public class InnerServer {
    public static void handle(HttpExchange exchange, String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String type = rootNode.get("type").asText();
        JsonNode dataNode = rootNode.get("data");

        try {
            switch (type) {
                case "data.Message":
                    Message msg;
                    msg = mapper.treeToValue(dataNode, Message.class);
                    Register(msg);
                    NetUtils.SendResponse(exchange, 400, "Given type mismatch", true);
                    Context.logger.Log("Got invalid object type", LogLevel.Error);
                    NetUtils.SendResponse(exchange, 200, "Object received", true);
                case "":
            }
        } catch (JsonProcessingException e) {
            NetUtils.SendResponse(exchange, 200, "Object received", true);
        } catch (IOException e) {

        }
        NetUtils.SendResponse(exchange, 200, "Object received", true);
    }
}
