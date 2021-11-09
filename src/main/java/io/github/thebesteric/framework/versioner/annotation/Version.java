package io.github.thebesteric.framework.versioner.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Version {
    String[] value();
}
