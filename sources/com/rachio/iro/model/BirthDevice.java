package com.rachio.iro.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.rachio.iro.model.annotation.RestClientOptions;

@RestClientOptions(path = "/1/device/birth", timeout = 240000)
public class BirthDevice extends ModelObject {
    private static final long serialVersionUID = 1;
    @JsonView({TransmittableView.class})
    public String externalUrl;
    @JsonView({TransmittableView.class})
    public double latitude;
    @JsonView({TransmittableView.class})
    public double longitude;
    @JsonView({TransmittableView.class})
    public Boolean masterValve;
    @JsonView({TransmittableView.class})
    public String name;
    @JsonView({TransmittableView.class})
    public PersonIdAndExternalPlanId person;
    @JsonView({TransmittableView.class})
    public String pin;
    @JsonView({TransmittableView.class})
    public String status;
    @JsonView({TransmittableView.class})
    public String timeZone;
    @JsonView({TransmittableView.class})
    public String zip;

    public String getExternalUrl() {
        return this.externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZip() {
        return this.zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPin() {
        return this.pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public PostId getPerson() {
        return this.person;
    }

    public void setPerson(PersonIdAndExternalPlanId user) {
        this.person = user;
    }
}
