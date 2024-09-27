package data;

import common.Context;
import data.encryption.entities.TDevice;
import data.requests.auth.AuthRequest;
import data.requests.auth.AuthResponse;
import data.requests.auth.AuthResult;
import network.NetUtils;

public class Registration {
    public static void Register(Message msg) {

    }

    public static AuthResponse Register(AuthRequest request, TDevice device) {
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
                Database.Register(
                        UserAccount.Register(request.Login, request.Authenticator, request.Verifier)
                                .PK(device.GetPublicKeyToClient()));
                return new AuthResponse().result(AuthResult.AUTH_SUCCESS).data(Database.UserByEmail(request.Authenticator));
        }
        return new AuthResponse().result(AuthResult.ERROR_PROCESSING);
    }
}
