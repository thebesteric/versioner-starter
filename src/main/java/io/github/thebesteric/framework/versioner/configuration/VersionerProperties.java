package io.github.thebesteric.framework.versioner.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = VersionerProperties.PROPERTIES_PREFIX)
public class VersionerProperties {

    public static final String PROPERTIES_PREFIX = "sourceflag.versioner";

    /** 总开关 */
    private boolean enable = true;
}
