package com.rachio.iro.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RestClientOptions {
    boolean appHeaders() default false;

    String arrayPath() default "";

    String path() default "";

    boolean shallow() default false;

    int timeout() default 30000;
}
