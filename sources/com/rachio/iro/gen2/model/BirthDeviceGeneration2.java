package com.rachio.iro.gen2.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.rachio.iro.model.ModelObject;
import com.rachio.iro.model.TransmittableView;
import com.rachio.iro.model.annotation.RestClientOptions;

@RestClientOptions(path = "/1/device/birth_generation2", timeout = 240000)
public class BirthDeviceGeneration2 extends ModelObject {
    @JsonView({TransmittableView.class})
    public String externalUrl = "mqtt.rach.io";
    @JsonView({TransmittableView.class})
    public double latitude;
    @JsonView({TransmittableView.class})
    public double longitude;
    @JsonView({TransmittableView.class})
    public String macAddress;
    @JsonView({TransmittableView.class})
    public boolean masterValve = false;
    @JsonView({TransmittableView.class})
    public String name;
    @JsonView({TransmittableView.class})
    public Person person;
    @JsonView({TransmittableView.class})
    public String status = "ONLINE";
    @JsonView({TransmittableView.class})
    public String timeZone;
    @JsonView({TransmittableView.class})
    public boolean waterHammer = false;
    @JsonView({TransmittableView.class})
    public String zip;

    public static class Person {
        @JsonView({TransmittableView.class})
        public String externalPlanId;
        @JsonView({TransmittableView.class})
        public String id;

        public Person(String id, String externalPlanId) {
            this.id = id;
            this.externalPlanId = externalPlanId;
        }
    }

    public BirthDeviceGeneration2(String name, String macAddress, String zip, String personId, String externalPlanId, double latitude, double longitude) {
        this.name = name;
        this.macAddress = macAddress.toUpperCase();
        this.zip = zip;
        this.person = new Person(personId, externalPlanId);
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
