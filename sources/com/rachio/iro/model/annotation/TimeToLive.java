package com.rachio.iro.model.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TimeToLive {
    long timeToEviction() default -1;

    long timeToLive() default 0;
}
