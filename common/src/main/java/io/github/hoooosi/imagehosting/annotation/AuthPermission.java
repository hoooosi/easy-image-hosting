package io.github.hoooosi.imagehosting.annotation;

import io.github.hoooosi.imagehosting.aop.auth.common.ID;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface AuthPermission {
    long mask();

    ID id();

    String SpEL() default "";
}
