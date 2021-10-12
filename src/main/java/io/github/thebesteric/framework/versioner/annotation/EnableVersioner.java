package io.github.thebesteric.framework.versioner.annotation;

import io.github.thebesteric.framework.versioner.configuration.VersionerMarker;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(VersionerMarker.class)
@Documented
public @interface EnableVersioner {
}
