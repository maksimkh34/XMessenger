package network;

import com.sun.net.httpserver.HttpExchange;

import java.security.PublicKey;

public class Package {
    public HttpExchange exchange;
    public int code;
    public String decryptedData;
    public boolean closeExchange;

    public Package() { }
    public Package(int code, String decryptedData, boolean closeExchange) {
        this.code = code;
        this.decryptedData = decryptedData;
        this.closeExchange = closeExchange;
    }

    public Package(HttpExchange exchange, int code, String decryptedData, boolean closeExchange) {
        this(code, decryptedData, closeExchange);
        this.exchange = exchange;
    }

    public Package exchange(HttpExchange exchange) {
        this.exchange = exchange;
        return this;
    }

    public Package closeExchange(boolean closeExchange) {
        this.closeExchange = closeExchange;
        return this;
    }

    public Package data(String data) {
        this.decryptedData = data;
        return this;
    }
}
