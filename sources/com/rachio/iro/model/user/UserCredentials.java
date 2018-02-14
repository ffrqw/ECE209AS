package com.rachio.iro.model.user;

import com.rachio.iro.model.ModelObject;
import java.util.HashMap;
import java.util.Map;

public class UserCredentials extends ModelObject {
    private static final long serialVersionUID = 1;
    public final String accessToken;
    public final String apiKey;
    public final String messagingAuthKey;
    public final String password;
    public final String secretKey;
    public final String username;

    public UserCredentials(String username, String password, String apiKey, String secretKey, String messagingAuthKey, String accessToken) {
        this.username = username;
        this.password = password;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.messagingAuthKey = messagingAuthKey;
        this.accessToken = accessToken;
    }

    public Map<String, String> getSessionKeys() {
        if (this.apiKey == null || this.secretKey == null) {
            return null;
        }
        Map<String, String> keys = new HashMap();
        keys.put("api-key", this.apiKey);
        keys.put("secret-key", this.secretKey);
        return keys;
    }
}
