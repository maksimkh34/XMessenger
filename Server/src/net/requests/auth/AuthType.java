package net.requests.auth;

public enum AuthType {
    AUTH_EMAIL,     // войти по почте и паролю
    AUTH_LOGIN,     // войти по логину и паролю
    AUTH_EMAIL_CODE, // войти по коду с почты
    REGISTER
}
