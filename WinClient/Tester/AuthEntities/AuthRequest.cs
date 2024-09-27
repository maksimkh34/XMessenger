using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Tester.AuthEntities
{
    internal class AuthRequest
    {
        public AuthType AuthType;
        public string? PermServerToClient { get; set; }
        public string? Login { get; set; }            // указывается только при регистрации
        public string? Authenticator { get; set; }    // поле, идентифицирующее пользователя. почта, логин, и т. д.
        public string? Verifier { get; set; }         // поле, подтверждающее идентификацию (пароль, код из почты, sms, и тд)
        // в случае AUTH_EMAIL_CODE, это поле будет пустым, т. к. сервер по принятию этого
        // объекта должен сам отправить код, а затем клиент отправит другой объект с кодом
    }
}
