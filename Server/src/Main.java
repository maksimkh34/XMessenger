import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.nio.charset.StandardCharsets;
public class Main {
    public static void main(String[] args) throws IOException {
        StartServer(1588, "/Server");
    }

    static class NetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            if ("POST".equals(exchange.getRequestMethod())) {
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                ObjectMapper mapper = new ObjectMapper();

                MyObject obj;
                try {
                    obj = mapper.readValue(json, MyObject.class);
                    Register(obj);
                } catch (IOException e) {
                    SendResponse(exchange, 400, "Invalid object type", true);
                }
                SendResponse(exchange, 200, "Object received", true);
            } else {
                SendResponse(exchange, 405, "Method not allowed (expected: POST)", true);
            }
        }
    }

    public static void Register(MyObject object) {
        System.out.println("Получен объект с id " + object.Id);
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
        server.setExecutor(null); // creates a default executor
        server.start();
    }
}
