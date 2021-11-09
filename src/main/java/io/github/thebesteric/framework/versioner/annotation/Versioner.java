package io.github.thebesteric.framework.versioner.annotation;

import io.github.thebesteric.framework.versioner.domain.NoneType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Versioner {
    String value();

    String key() default "";

    Class<?> type() default NoneType.class;
}
