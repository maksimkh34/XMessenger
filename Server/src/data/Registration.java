package data;

import common.Context;
import data.encryption.entities.CanDecrypt;
import data.requests.auth.AuthRequest;
import data.requests.auth.AuthResponse;
import data.requests.auth.AuthResult;
import network.Cryptography;
import network.NetUtils;

public class Registration {
    public static void Register(Message msg) {

    }

    public static AuthResponse HandleAuthRequest(AuthRequest request) {
        switch (request.AuthType) {
            case AUTH_EMAIL:
                var email = request.Authenticator;
                if(Context.database.TryLoginEmail(email, request.Verifier)) {
                    var response = new AuthResponse();
                    return response.result(AuthResult.AUTH_SUCCESS).data(Database.UserByEmail(email));
                } else {
                    return new AuthResponse().result(AuthResult.INVALID_PASSWORD);
                }
            case AUTH_EMAIL_CODE:
                var email_for_code = request.Authenticator;
                var code = NetUtils.sendAuthCode(email_for_code);
                Context.database.WaitForEmailCode(email_for_code, code);
                return new AuthResponse().result(AuthResult.EMAIL_WAITING_CODE);
            case REGISTER_EMAIL:
                var ctsPair = Cryptography.generateKeyPair();
                Database.Register(
                        UserAccount.Register(request.Login, request.Authenticator,
                                request.Verifier, request.PermServerToClient, ctsPair.getPrivate())
                                );
                return new AuthResponse().result(AuthResult.AUTH_SUCCESS)
                        .data(Database.UserByEmail(request.Authenticator))
                        .permCts(Cryptography.publicKeyToString(ctsPair.getPublic()));
        }
        return new AuthResponse().result(AuthResult.ERROR_PROCESSING);
    }
}
