using Newtonsoft.Json;
using Web;

namespace Tester;

internal class Program
{
    private static async Task Main(string[] args)
    {
        Config.Init();
        /*
        var keys = Cryptography.GenerateKeyPair(); 
        var response = await NetManager.Send("TPKeyRequest:" + keys.PublicKey);
        if (response.StatusCode == 200)
        {
            NetManager.DeviceId = response.Message[..8];
            NetManager.PublicKey = Cryptography.DecryptJson(response.Message, keys.PrivateKey).Replace(NetManager.DeviceId, "");
        }
        */
        const string json = "208o3uh09v246t0973q46tgn91734tg90871347t98012f34m9r82u3rfw5rfвапвапвапвапвапg6777^6^^^^^^^";
        var keys = KeyGeneratorUtil.GenerateKeyPair();
        var encryptedJson = Cryptography.EncryptJson(keys.publicKey, json);
        var decryptedJson = Cryptography.DecryptJson(keys.privateKey, encryptedJson);
        Console.WriteLine("Decrypted json:\n\n" + json + "\n\n");
        Console.WriteLine("Encrypted json:\n\n" + encryptedJson + "\n\n");
        Console.WriteLine("Decrypted: " + (json == decryptedJson));
    }
}