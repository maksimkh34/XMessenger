package data.requests.auth;

import data.UserAccount;

public class AuthResponse {
    public AuthResult Result;
    public UserAccount Data;
    public AuthResponse result(AuthResult result) {var a = this; a.Result = result; return a;}
    public AuthResponse data(UserAccount data) {var a = this; a.Data = data; return a;}
}
