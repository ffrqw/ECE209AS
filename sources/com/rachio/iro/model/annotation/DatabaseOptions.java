package com.rachio.iro.model.annotation;

import com.rachio.iro.model.db.DatabaseObject;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseOptions {
    String altparentcol() default "";

    String broadcast() default "";

    Class<? extends DatabaseObject>[] descendants() default {};

    Class<? extends DatabaseObject> parent() default DatabaseObject.class;

    String parentcol() default "";
}
