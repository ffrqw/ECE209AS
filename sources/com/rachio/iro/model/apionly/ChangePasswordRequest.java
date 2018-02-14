package com.rachio.iro.model.apionly;

import com.fasterxml.jackson.annotation.JsonView;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.annotation.RestClientOptions;

@RestClientOptions(path = "/1/person/password_change")
public class ChangePasswordRequest extends ModelObject {
    private static final long serialVersionUID = 1;
    @JsonView({TransmittableView.class})
    public final String id;
    @JsonView({TransmittableView.class})
    public final String newPassword;
    @JsonView({TransmittableView.class})
    public final String newPasswordConfirm;
    @JsonView({TransmittableView.class})
    public final String oldPassword;

    public ChangePasswordRequest(String id, String oldPassword, String newPassword, String newPasswordConfirm) {
        this.id = id;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.newPasswordConfirm = newPasswordConfirm;
    }
}
