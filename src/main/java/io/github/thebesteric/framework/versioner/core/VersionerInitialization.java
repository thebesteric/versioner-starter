package io.github.thebesteric.framework.versioner.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.support.GenericApplicationContext;

@Slf4j
public class VersionerInitialization implements SmartLifecycle, ApplicationContextAware {

    protected GenericApplicationContext applicationContext;

    protected boolean isRunning = false;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (GenericApplicationContext) applicationContext;
    }

    @Override
    public void start() {
        this.isRunning = true;
        log.info("Versioner is running");
    }

    @Override
    public void stop() {
        this.isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return this.isRunning;
    }
}
