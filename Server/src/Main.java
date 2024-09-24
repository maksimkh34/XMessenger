import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.nio.charset.StandardCharsets;
public class Main {
    static Logger logger = new Logger();
    public static void main(String[] args) throws IOException {
        Config.load();
        logger.Log("Server version: " + Config.getValue(Config.SERVER_VERSION), LogLevel.Info);
        StartServer(Integer.parseInt(Config.getValue(Config.SERVER_PORT)), Config.getValue(Config.SERVER_PATH));
        logger.Log("Started.", LogLevel.Info);
    }

    static class NetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            logger.Log("Got HttpExchange!", LogLevel.Info);
            InputStream is = exchange.getRequestBody();
            if ("POST".equals(exchange.getRequestMethod())) {
                logger.Log("Type: POST", LogLevel.Info);
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                JsonNode rootNode = mapper.readTree(json);
                String type = rootNode.get("type").asText();
                JsonNode dataNode = rootNode.get("data");

                switch (type) {
                    case "Message":
                        Message msg;
                        try {
                            msg = mapper.treeToValue(dataNode, Message.class);
                            Register(msg);
                        } catch (IOException e) {
                            SendResponse(exchange, 400, "Given type mismatch", true);
                            logger.Log("Got invalid object type", LogLevel.Error);
                        }
                        SendResponse(exchange, 200, "Object received", true);
                    case "":
                }
                SendResponse(exchange, 200, "Object received", true);
            } else {
                logger.Log("Expected POST, got " + exchange.getRequestMethod(), LogLevel.Warning);
                SendResponse(exchange, 405,
                        "Method not allowed (expected: POST, got: " + exchange.getRequestMethod() + ")",
                        true);
            }
        }
    }

    public static void Register(Message object) throws IOException {
        logger.Log("Got new message: \"" + object.Text + "\", date: "
                + object.SentTime.toString() + ", sender: " + object.SenderId, LogLevel.Info);
    }

    public static void SendResponse(HttpExchange exchange, int code, String message, Boolean close) throws IOException {
        exchange.sendResponseHeaders(code, message.length());
        OutputStream os = exchange.getResponseBody();
        os.write(message.getBytes());
        if(close) os.close();
    }

    public static void StartServer(int port, String path) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext(path, new NetHandler());
        server.setExecutor(null);
        logger.Log("Server starting...", LogLevel.Info);
        server.start();
    }
}
