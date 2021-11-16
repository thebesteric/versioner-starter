package io.github.thebesteric.framework.versioner.configuration;

import io.github.thebesteric.framework.versioner.core.VersionHandler;
import io.github.thebesteric.framework.versioner.core.VersionManager;
import io.github.thebesteric.framework.versioner.core.VersionerInitialization;
import io.github.thebesteric.framework.versioner.filter.VersionerFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Import(VersionerInitialization.class)
@EnableAsync
@ConditionalOnBean(VersionerMarker.class)
@EnableConfigurationProperties(VersionerProperties.class)
public class VersionerAutoConfiguration implements WebMvcConfigurer {

    private static final String FILTER_NAME = "versionFilter";
    private static final Integer FILTER_ORDER = 1;
    private static final String FILTER_URL_PATTERNS = "/*";

    @Bean
    @SuppressWarnings({"unchecked", "rawtypes"})
    public FilterRegistrationBean filterRegister(VersionManager versionManager, VersionerProperties properties) {
        FilterRegistrationBean frBean = new FilterRegistrationBean();
        frBean.setName(FILTER_NAME);
        frBean.setOrder(FILTER_ORDER);
        frBean.addUrlPatterns(FILTER_URL_PATTERNS);
        frBean.setFilter(new VersionerFilter(versionManager, properties));
        return frBean;
    }

    @Bean
    public VersionManager versionManager() {
        return new VersionManager();
    }

    @Bean
    public VersionHandler versionHandler(VersionManager versionManager) {
        return new VersionHandler(versionManager);
    }
}
