using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Web;

namespace Tester;

internal class Program
{
    private static async Task Main(string[] args)
    {
        Config.Init();
        var keys = KeyGeneratorUtil.GenerateKeyPair(); 
        var response = await NetManager.Send("TPKeyRequest:" + KeyGeneratorUtil.PublicKeyToString(keys.publicKey));
        if (response.StatusCode == 200)
        {
            var decryptedJsonStr = Cryptography.DecryptJson(keys.privateKey, response.Message);
            var jsonObject = JObject.Parse(decryptedJsonStr);
            var dataValue = jsonObject["data"]?.ToString() ?? "";
            if (dataValue.StartsWith("\"") && dataValue.EndsWith("\""))
            {
                dataValue = dataValue.Substring(1, dataValue.Length - 2);
            }
            NetManager.DeviceId = dataValue[..8];
            NetManager.PublicKey = dataValue[8..];
        }
    }
}