import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import org.jose4j.jwe.JsonWebEncryption;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
public class Main {

    static Logger logger = new Logger();
    static KeyPair keys;
    public static void main(String[] args) throws IOException {
        Config.load();
        keys = generateKeyPair();
        logger.Log("Server version: " + Config.getValue(Config.SERVER_VERSION), LogLevel.Info);
        StartServer(Integer.parseInt(Config.getValue(Config.SERVER_PORT)), Config.getValue(Config.SERVER_PATH));
        logger.Log("Started.", LogLevel.Info);
    }

    public static String GetPublicKey() {
        return Base64.getEncoder().encodeToString(keys.getPublic().getEncoded());
    }

    public static String GetPrivateKey() {
        return Base64.getEncoder().encodeToString(keys.getPrivate().getEncoded());
    }

    public static String decryptJson(String encryptedJson, String privateKeyStr) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        JsonWebEncryption jwe = new JsonWebEncryption();
        jwe.setCompactSerialization(encryptedJson);
        jwe.setKey(privateKey);

        return jwe.getPayload();
    }

    public static KeyPair generateKeyPair() {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    static class NetHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            logger.Log("Got HttpExchange!", LogLevel.Info);
            InputStream is = exchange.getRequestBody();
            if ("POST".equals(exchange.getRequestMethod())) {
                logger.Log("Type: POST", LogLevel.Info);
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                if(json.equals("PublicKeyRequest")) {
                    logger.Log("Got public key request", LogLevel.Info);
                    SendResponse(exchange, 200, GetPublicKey(), true);
                    return;
                }

                try {
                    json = decryptJson(json, GetPrivateKey());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                String receivedHmac = exchange.getRequestHeaders().getFirst("X-HMAC-Signature");
                if (!verifyHMAC(json, receivedHmac)) {
                    SendResponse(exchange, 401, "Unauthorized", true);
                    logger.Log("Invalid HMAC signature", LogLevel.Error);
                    return;
                }

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

    private static boolean verifyHMAC(String data, String receivedHmac) {
        try {
            Mac mac = Mac.getInstance(Config.getValue(Config.HMAC_ALGO));
            SecretKeySpec secretKeySpec = new SecretKeySpec(Config.getValue(Config.HMAC_KEY).getBytes(), Config.getValue(Config.HMAC_ALGO));
            mac.init(secretKeySpec);
            byte[] hmacBytes = mac.doFinal(data.getBytes());
            String calculatedHmac = Base64.getEncoder().encodeToString(hmacBytes);
            return calculatedHmac.equals(receivedHmac);
        } catch (Exception e) {
            logger.Log("HMAC verification failed", LogLevel.Error);
            return false;
        }
    }
}
