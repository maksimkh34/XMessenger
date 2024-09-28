using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Web;
using Web.NetInteraction;

namespace Tester;

internal class Program
{
    private static async Task Main(string[] args)
    {
        Config.Init();
        var tempStcKeys = KeyGeneratorUtil.GenerateKeyPair(); 
        var response = await NetManager.Send("TPKeyRequest:" + KeyGeneratorUtil.PublicKeyToString(tempStcKeys.publicKey));
        if (response.StatusCode == 200)
        {
            var decryptedJsonStr = Cryptography.DecryptJson(tempStcKeys.privateKey, response.Message)!;
            NetManager.DeviceId = decryptedJsonStr[..8];
            NetManager.HmacKey = NetManager.DeviceId;
            NetManager.PublicKeyToServer = decryptedJsonStr[8..];
        }

        var stcPermKeyPair = KeyGeneratorUtil.GenerateKeyPair();
        var request = new AuthRequest
            { Authenticator = "email@gmail.com", Verifier = "password", AuthType = AuthType.AUTH_EMAIL, Login = "MyLogin", PermServerToClient = KeyGeneratorUtil.PublicKeyToString(stcPermKeyPair.publicKey) };
        var rawResponse = await NetManager.Send(request);
        if (rawResponse.StatusCode != 200) return;
        var authResponse = Cryptography.Parse<AuthResponse>(stcPermKeyPair.privateKey, rawResponse.Message);
        if (authResponse.Result == AuthResult.AUTH_SUCCESS)
        {
            Config.CurrentUser = authResponse.Data;
            NetManager.DeviceId = null;
            NetManager.HmacKey = Config.CurrentUser?.Id + Config.CurrentUser?.Secret;
            NetManager.PublicKeyToServer = authResponse.PermCts;
        }
    }
}