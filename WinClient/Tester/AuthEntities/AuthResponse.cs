using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
using Web;

namespace Tester.AuthEntities
{
    public class AuthResponse
    {
        [JsonConverter(typeof(AuthResultConverter))]
        public AuthResult? Result { get; set; }
        public UserData? Data { get; set; }
        public string? PermCts { get; set; }
    }
}
