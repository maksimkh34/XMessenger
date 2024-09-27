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
        var keys = KeyGeneratorUtil.GenerateKeyPair(); 
        var response = await NetManager.Send("TPKeyRequest:" + KeyGeneratorUtil.PublicKeyToString(keys.publicKey));
        if (response.StatusCode == 200)
        {
            var decryptedJsonStr = Cryptography.DecryptJson(keys.privateKey, response.Message)!;
            NetManager.DeviceId = decryptedJsonStr[..8];
            NetManager.PublicKeyToServer = decryptedJsonStr[8..];
            NetManager.HmacKey = NetManager.DeviceId;
        }

        var request = new AuthRequest
            { Authenticator = "email@gmail.com", Verifier = "password", AuthType = AuthType.REGISTER_EMAIL, Login = "MyLogin"};
        var rawResponse = await NetManager.Send(request);
        if (rawResponse.StatusCode != 200) return;
        var jsonResponse = Cryptography.DecryptJson(keys.privateKey, rawResponse.Message);
        var authResponse = JsonConvert.DeserializeObject<AuthResponse>(jsonResponse!);
        if (authResponse?.Result == AuthResult.AUTH_SUCCESS)
        {
            Config.CurrentUser = authResponse.Data;
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
        var validResult = await NetManager.Send(validLoginRequest);

        var dInvalidResult = Cryptography.DecryptJson(keys.privateKey, invalidResult.Message);
        var dValidResult = Cryptography.DecryptJson(keys.privateKey, validResult.Message);
    }
}