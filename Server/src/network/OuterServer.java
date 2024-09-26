package network;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import common.Context;
import common.LogLevel;

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
                if (json.equals("PublicKeyRequest")) {
                    Context.logger.Log("Got public key request", LogLevel.Info);
                    NetUtils.SendResponse(exchange, 200, Cryptography.GetPublicKey(), true);
                    return;
                }

                try {
                    json = Cryptography.decryptJson(json, Cryptography.GetPrivateKey());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                String receivedHmac = exchange.getRequestHeaders().getFirst("X-HMAC-Signature");
                if (!Cryptography.verifyHMAC(json, receivedHmac)) {
                    NetUtils.SendResponse(exchange, 401, "Unauthorized", true);
                    Context.logger.Log("Invalid HMAC signature", LogLevel.Error);
                    return;
                }
                InnerServer.handle(exchange, json);
            } else {
                Context.logger.Log("Expected POST, got " + exchange.getRequestMethod(), LogLevel.Warning);
                NetUtils.SendResponse(exchange, 405,
                        "Method not allowed (expected: POST, got: " + exchange.getRequestMethod() + ")",
                        true);
            }
        }

    }
}
