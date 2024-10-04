namespace Web
{
    public class NetResponse(uint statusCode, string message)
    {
        public uint StatusCode { get; set; } = statusCode;
        public string Message { get; set; } = message;
    }
}
