package com.github.session.jwt.annotations;

import com.github.session.jwt.config.SpringSessionJwtConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SpringSessionJwtConfig.class)
public @interface EnableSpringSessionJwt {
}
