package ru.hse.lmsteam.backend.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks custom converter, to be registered in project with {@link org.springframework.data.r2dbc.core.R2dbcEntityTemplate}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CustomConverter {}
