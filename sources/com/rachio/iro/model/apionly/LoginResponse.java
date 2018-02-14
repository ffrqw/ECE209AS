package com.rachio.iro.model.apionly;

import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.user.UserCredentials;

public class LoginResponse extends ModelObject implements ErrorResponse {
    private static final long serialVersionUID = 1;
    public String accessToken;
    public String apiKey;
    public boolean loggedIn;
    public LoginRequest loginRequest;
    public String message;
    public String messagingAuthKey;
    public String secretKey;
    public String userId;
    public String username;

    public UserCredentials getUserCredentials() {
        return new UserCredentials(this.username, this.loginRequest != null ? this.loginRequest.password : null, this.apiKey, this.secretKey, this.messagingAuthKey, this.accessToken);
    }

    public void setError(String error) {
    }

    public String getError() {
        return this.message;
    }

    public void setCode(int code) {
    }

    public int getCode() {
        return 0;
    }

    public boolean hasError() {
        return !this.loggedIn;
    }
}
