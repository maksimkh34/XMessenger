using Context;
using Newtonsoft.Json;

namespace Web.NetInteraction
{
    public class AuthResponse
    {
        [JsonConverter(typeof(AuthResultConverter))]
        public AuthResult? Result { get; set; }
        public UserData? Data { get; set; }
        public string? PermCts { get; set; }
    }
}
