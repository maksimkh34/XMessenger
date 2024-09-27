using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Tester.AuthEntities;
using Web;

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
            { Authenticator = "email@gmail.com", Verifier = "password", AuthType = AuthType.REGISTER_EMAIL, Login = "MyLogin", PermServerToClient = KeyGeneratorUtil.PublicKeyToString(stcPermKeyPair.publicKey) };
        var rawResponse = await NetManager.Send(request);
        if (rawResponse.StatusCode != 200) return;
        var jsonResponse = Cryptography.DecryptJson(stcPermKeyPair.privateKey, rawResponse.Message);
        var authResponse = JsonConvert.DeserializeObject<AuthResponse>(jsonResponse);
        //var authResponse = Cryptography.Parse<AuthRequest>(stcPermKeyPair.privateKey, rawResponse.Message);
        if (authResponse?.Result == AuthResult.AUTH_SUCCESS)
        {
            Config.CurrentUser = authResponse.Data;
            NetManager.DeviceId = null;
            NetManager.HmacKey = Config.CurrentUser?.Id + Config.CurrentUser?.Secret;
            NetManager.PublicKeyToServer = authResponse.PermCts;
        }

        var invalidLoginRequest = new AuthRequest
        {
            Authenticator = "email@gmail.com",
            Verifier = "password2",
            AuthType = AuthType.AUTH_EMAIL
        };

        var validLoginRequest = new AuthRequest
        {
            Authenticator = "email@gmail.com",
            Verifier = "password",
            AuthType = AuthType.AUTH_EMAIL
        };

        var invalidResult = await NetManager.Send(invalidLoginRequest);
        var dInvalidResult = Cryptography.DecryptJson(stcPermKeyPair.privateKey, invalidResult.Message);

        var validResult = await NetManager.Send(validLoginRequest);
        var dValidResult = Cryptography.DecryptJson(stcPermKeyPair.privateKey, validResult.Message);

        var invalidResult2 = await NetManager.Send(invalidLoginRequest);
        var dInvalidResult2 = Cryptography.DecryptJson(stcPermKeyPair.privateKey, invalidResult.Message);
    }
}