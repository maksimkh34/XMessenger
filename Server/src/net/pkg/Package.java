package net.pkg;

import com.sun.net.httpserver.HttpExchange;

public class Package {
    public HttpExchange exchange;
    public int code;
    public String decryptedData;
    public boolean closeExchange;

    public Package(HttpExchange exchange) {
        this.exchange = exchange;
    }
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
        var p = this;
        p.exchange = exchange;
        return p;
    }

    public Package closeExchange(boolean closeExchange) {
        var p = this;
        p.closeExchange = closeExchange;
        return p;
    }

    public Package decryptedData(String data) {
        var p = this;
        p.decryptedData = data;
        return p;
    }
}
