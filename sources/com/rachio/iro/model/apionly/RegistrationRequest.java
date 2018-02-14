package com.rachio.iro.model.apionly;

import com.fasterxml.jackson.annotation.JsonView;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.annotation.RestClientOptions;

@RestClientOptions(appHeaders = true, path = "/1/person/add")
public class RegistrationRequest extends ModelObject {
    private static final long serialVersionUID = 1;
    @JsonView({TransmittableView.class})
    public final String email;
    @JsonView({TransmittableView.class})
    public final String fullName;
    @JsonView({TransmittableView.class})
    public final String password;
    @JsonView({TransmittableView.class})
    public final String username;

    public RegistrationRequest(String fullName, String email, String username, String password) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
