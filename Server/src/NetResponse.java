public class NetResponse
{
    public int StatusCode;
    public String Message;

    public NetResponse(int statusCode, String message) {
        this.StatusCode = statusCode;
        this.Message = message;
    }

    public NetResponse() {
        this(-1, "");
    }
}