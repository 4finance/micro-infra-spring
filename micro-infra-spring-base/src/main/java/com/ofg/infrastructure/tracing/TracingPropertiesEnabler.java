package com.ofg.infrastructure.tracing;

import com.ofg.config.BasicProfiles;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

import java.util.Arrays;

/**
 * Class that sets tracing related properties
 */
@Configuration
public class TracingPropertiesEnabler implements ApplicationContextAware,
        BeanFactoryAware, SmartInitializingSingleton {

    ApplicationContext applicationContext;
    BeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {
        if (!(applicationContext.getEnvironment() instanceof ConfigurableEnvironment)) {
            return;
        }
        final ConfigurableEnvironment environment = (ConfigurableEnvironment) applicationContext.getEnvironment();
        environment.getPropertySources().addLast(
                new PropertySource<Object>("Spring Zipkin Enabling") {
                    @Override
                    public Object getProperty(String name) {
                        if ("spring.zipkin.enabled".equals(name)) {
                            return Arrays.asList(environment.getActiveProfiles()).contains(BasicProfiles.PRODUCTION);
                        }
                        return null;
                    }
                });
    }
}
