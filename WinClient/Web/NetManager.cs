using System.Security.Cryptography;
using System.Text;
using Newtonsoft.Json;
using Jose;

namespace Web
{
    public static class NetManager
    {
        private static readonly string ServerUrl = $"http://{Config.GetValue(Config.ServerIP)}" +
                                                   $":{Config.GetValue(Config.ServerPort)}{Config.GetValue(Config.ServerPath)}";
        private static readonly HttpClient Client = new();
        public static string? PublicKeyToServer;
        public static string? DeviceId;
        public static string HmacKey = Config.GetValue(Config.HmacDefaultKey);

        public static async Task<NetResponse> Send<T>(T obj)
        {
            var json = JsonConvert.SerializeObject(new { type = obj?.GetType().Name, data = obj }, new JsonSerializerSettings()
            {
                DateFormatHandling = DateFormatHandling.IsoDateFormat,
                DateTimeZoneHandling = DateTimeZoneHandling.Utc
            });

            if (PublicKeyToServer != null)
            {
                json = EncryptJson(json, PublicKeyToServer);
            }

            var hmacSignature = GenerateHmac(json, HmacKey);
            var content = new StringContent(json, Encoding.UTF8, "application/json");
            content.Headers.Add("X-HMAC-Signature", hmacSignature);
            if(DeviceId != null) content.Headers.Add("DeviceId", DeviceId);

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

        private static string GenerateHmac(string data, string dataHmacKey)
        {
            using var hmac = new HMACSHA256(Encoding.UTF8.GetBytes(dataHmacKey));
            var hmacBytes = hmac.ComputeHash(Encoding.UTF8.GetBytes(data));
            return Convert.ToBase64String(hmacBytes);
        }

        public static string EncryptJson(string json, string publicKey)
        {
            var rsa = RSA.Create();
            rsa.ImportSubjectPublicKeyInfo(Convert.FromBase64String(publicKey), out _);
            var encryptedJson = JWT.Encode(json, rsa, JweAlgorithm.RSA_OAEP, JweEncryption.A256GCM);
            return encryptedJson;
        }
    }
}
