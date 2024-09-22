using System.Text;
using Newtonsoft.Json;


namespace WebTest
{
    public class MyObject
    {
        public int Id { get; set; }
        public string? Name { get; set; }
    }

    public class Program
    {
        public static Task Main(string[] args)
        {
            var response = NetManager.Send(new MyObject {Id=55, Name = "Test!"}).Result;
            Console.WriteLine($"Code {response.StatusCode}, Msg: {response.Message}. ");
            return Task.CompletedTask;
        }
    }

    public static class NetManager
    {
        private const string ServerUrl = @"http://localhost:1588/Server";
        private static readonly HttpClient Client = new();

        public static async Task<NetResponse> Send<T>(T obj)
        {
            var json = JsonConvert.SerializeObject(obj);
            var content = new StringContent(json, Encoding.UTF8, "application/json");

            var response = await Client.PostAsync(ServerUrl, content);
            var netResponse =
                new NetResponse((uint)response.StatusCode, await response.Content.ReadAsStringAsync());

            return netResponse;
        }
    }

    public class NetResponse(uint statusCode, string message)
    {
        public uint StatusCode { get; set; } = statusCode;
        public string Message { get; set; } = message;
    }
}