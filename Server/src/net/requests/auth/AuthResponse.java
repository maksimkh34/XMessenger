package net.requests.auth;

import entities.UserAccount;

public class AuthResponse {
    public AuthResult Result;
    public UserAccount Data;
    public String PermCts;
    public AuthResponse result(AuthResult result) {var a = this; a.Result = result; return a;}
    public AuthResponse data(UserAccount data) {var a = this; a.Data = data; return a;}
    public AuthResponse permCts(String PermCts) {var a = this; a.PermCts = PermCts; return a;}
}
