package data.util;

import data.logging.LogLevel;
import net.cryptography.KeysFactory;
import data.context.Context;
import data.database.Database;
import net.requests.auth.AuthRequest;
import net.requests.auth.AuthResponse;
import net.requests.auth.AuthResult;
import net.NetUtils;
import entities.UserAccount;

public class Registration {

    public static AuthResponse HandleAuthRequest(AuthRequest request) {
        switch (request.AuthType) {
            case AUTH_EMAIL:
                var email = request.Authenticator;
                var loginResult = Context.database.tryLoginEmail(email, request.Verifier);
                if(loginResult == null) return new AuthResponse().result(AuthResult.USER_NOT_REGISTERED);
                if(loginResult) {
                    var response = new AuthResponse();
                    return response.result(AuthResult.AUTH_SUCCESS).data(Database.userByEmail(email));
                } else {
                    return new AuthResponse().result(AuthResult.INVALID_PASSWORD);
                }
            case AUTH_EMAIL_CODE:
                var email_for_code = request.Authenticator;
                var code = NetUtils.sendAuthCode(email_for_code);
                Context.database.waitForEmailCode(email_for_code, code);
                return new AuthResponse().result(AuthResult.EMAIL_WAITING_CODE);
            case REGISTER:
                var ctsPair = KeysFactory.generateKeyPair();
                Database.register(
                        UserAccount.register(request.Login, request.Authenticator,
                                request.Verifier, request.PermServerToClient, ctsPair.getPrivate())
                                );
                Context.logger.Log("Registered new user: " + request.Authenticator, LogLevel.Info);
                return new AuthResponse().result(AuthResult.AUTH_SUCCESS)
                        .data(Database.userByEmail(request.Authenticator))
                        .permCts(KeysFactory.publicKeyToString(ctsPair.getPublic()));
        }
        return new AuthResponse().result(AuthResult.ERROR_PROCESSING);
    }
}
