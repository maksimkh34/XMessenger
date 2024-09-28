package net.requests.auth;

public class AuthRequest {
    public net.requests.auth.AuthType AuthType;
    public String PermServerToClient;
    public String Login;            // заполняется только при регистрации
    public String Authenticator;    // поле, идентифицирующее пользователя. почта, логин, и т. д.
    public String Verifier;         // поле, подтверждающее идентификацию (пароль, код из почты, sms, и тд)
                                    // в случае AUTH_EMAIL_CODE, это поле будет пустым, т. к. сервер по принятию этого
                                    // объекта должен сам отправить код, а затем клиент отправит другой объект с кодом
}
