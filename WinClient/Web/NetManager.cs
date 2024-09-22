using System.Text;
using Newtonsoft.Json;

namespace Web
{
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
}
