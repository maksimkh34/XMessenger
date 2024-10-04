using Context;
using Model.Status;
using Web;
using Web.NetInteraction;

namespace Model
{
    public static class Client
    {
        public static async Task<AuthResponse> Register(string email, string login, string password)
        {
            var request = new AuthRequest
            {
                Authenticator = email,
                AuthType = AuthType.REGISTER,
                Verifier = password,
                Login = login,
                PermServerToClient = KeyGeneratorUtil.PublicKeyToString(NetManager.StcPermPublicKey)
            };
            var rawResponse = await NetManager.Send(request);
            var authResponse = Cryptography.Parse<AuthResponse>(NetManager.StcPermPrivateKey, rawResponse.Message);
            if (authResponse.Result != AuthResult.AUTH_SUCCESS) return authResponse;
            Config.CurrentUser = authResponse.Data;
            NetManager.DeviceId = null;
            NetManager.HmacKey = Config.CurrentUser?.Id + Config.CurrentUser?.Secret;
            NetManager.PublicKeyToServer = authResponse.PermCts;
            return authResponse;
        }

        public static async Task<bool> TrySendOnline()
        {
            var rq = new StatusRq
            {
                ReceiverId = "11",
                Status = Status.Status.Online
            };
            var rawResponse = await NetManager.Send(rq);
            var response = Cryptography.Parse<string>(NetManager.StcPermPrivateKey, rawResponse.Message);
            return response == "ok";
        }

        public static async Task<AuthResponse> LoginEmail(string email, string password)
        {
            var request = GetAuthRequest(AuthType.AUTH_EMAIL, email, password);
            var rawResponse = await NetManager.Send(request);
            var authResponse = Cryptography.Parse<AuthResponse>(NetManager.StcPermPrivateKey, rawResponse.Message);
            if (authResponse.Result != AuthResult.AUTH_SUCCESS) return authResponse;
            Config.CurrentUser = authResponse.Data;
            NetManager.DeviceId = null;
            NetManager.HmacKey = Config.CurrentUser?.Id + Config.CurrentUser?.Secret;
            NetManager.PublicKeyToServer = authResponse.PermCts;
            return authResponse;
        }

        public static async Task<AuthResponse> LoginLogin(string login, string password)
        {
            var request = new AuthRequest
            {
                Authenticator = login,
                AuthType = AuthType.AUTH_LOGIN,
                Verifier = password,
                PermServerToClient = KeyGeneratorUtil.PublicKeyToString(NetManager.StcPermPublicKey)
            };
            var rawResponse = await NetManager.Send(request);
            var authResponse = Cryptography.Parse<AuthResponse>(NetManager.StcPermPrivateKey, rawResponse.Message);
            if (authResponse.Result != AuthResult.AUTH_SUCCESS) return authResponse;
            Config.CurrentUser = authResponse.Data;
            NetManager.DeviceId = null;
            NetManager.HmacKey = Config.CurrentUser?.Id + Config.CurrentUser?.Secret;
            NetManager.PublicKeyToServer = authResponse.PermCts;
            return authResponse;
        }

        public static AuthRequest GetAuthRequest(AuthType type, string email, string password)
        {
            return new AuthRequest
            {
                Authenticator = email,
                AuthType = type,
                Verifier = password,
                PermServerToClient = KeyGeneratorUtil.PublicKeyToString(NetManager.StcPermPublicKey)
            };

        }
    }
}
