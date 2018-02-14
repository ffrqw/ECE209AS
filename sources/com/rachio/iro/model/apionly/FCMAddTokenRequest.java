package com.rachio.iro.model.apionly;

import com.fasterxml.jackson.annotation.JsonView;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.annotation.RestClientOptions;

@RestClientOptions(path = "/1/fcm/addToken")
public class FCMAddTokenRequest extends ModelObject {
    @JsonView({TransmittableView.class})
    public String fcmTokenType = "ANDROID";
    @JsonView({TransmittableView.class})
    public String personId;
    @JsonView({TransmittableView.class})
    public String token;
}
