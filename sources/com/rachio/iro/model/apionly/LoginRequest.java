package com.rachio.iro.model.apionly;

import com.fasterxml.jackson.annotation.JsonView;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.annotation.RestClientOptions;

@RestClientOptions(appHeaders = true, path = "/1/person/login")
public class LoginRequest extends ModelObject {
    private static final long serialVersionUID = 1;
    @JsonView({TransmittableView.class})
    public final String password;
    @JsonView({TransmittableView.class})
    public final String username;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
