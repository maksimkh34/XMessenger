using System.Security.Cryptography;
using System.Text;
using Newtonsoft.Json;

namespace Web
{
    public static class NetManager
    {
        private static readonly string ServerUrl = $"http://{Config.GetValue(Config.ServerIP)}" +
                                                   $":{Config.GetValue(Config.ServerPort)}{Config.GetValue(Config.ServerPath)}";
        private static readonly HttpClient Client = new();

        public static async Task<NetResponse> Send<T>(T obj)
        {
            var json = JsonConvert.SerializeObject(new { type = obj?.GetType().Name, data = obj }, new JsonSerializerSettings()
            {
                DateFormatHandling = DateFormatHandling.IsoDateFormat,
                DateTimeZoneHandling = DateTimeZoneHandling.Utc
            });
            var content = new StringContent(json, Encoding.UTF8, "application/json");
            var hmacSignature = GenerateHmac(json);
            content.Headers.Add("X-HMAC-Signature", hmacSignature);

            HttpResponseMessage? response;
            try
            {
                response = await Client.PostAsync(ServerUrl, content);
            }
            catch (AggregateException)
            {
                throw new AggregateException($"Конечный сервер недоступен (URL: {ServerUrl}). ");
            }
            var netResponse =
                new NetResponse((uint)response.StatusCode, await response.Content.ReadAsStringAsync());

            return netResponse;
        }

        private static string GenerateHmac(string data)
        {
            using var hmac = new HMACSHA256(Encoding.UTF8.GetBytes(Config.GetValue(Config.HMACDefaultKey)));
            var hmacBytes = hmac.ComputeHash(Encoding.UTF8.GetBytes(data));
            return Convert.ToBase64String(hmacBytes);
        }
    }
}
