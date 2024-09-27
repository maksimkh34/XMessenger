package data.requests.auth;

public enum AuthType {
    AUTH_EMAIL,     // войти по почте и паролю
    AUTH_LOGIN,     // войти по логину и паролю
    AUTH_EMAIL_CODE,// войти по почте и коду с почты
    REGISTER_EMAIL  // зарегистрироваться с почтой и паролем
}
