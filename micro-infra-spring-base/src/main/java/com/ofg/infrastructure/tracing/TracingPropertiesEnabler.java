package com.ofg.infrastructure.tracing;

import com.ofg.config.BasicProfiles;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * Class that sets tracing related properties
 */
@Configuration
@ConditionalOnProperty(value = "tracing.properties.enabled", matchIfMissing = true)
public class TracingPropertiesEnabler implements ApplicationContextAware,
        BeanFactoryAware, SmartInitializingSingleton {

    static final String ENVIRONMENT_PROPERTY = "APP_ENV";
    static final String SPRING_ZIPKIN_ENABLED = "spring.zipkin.enabled";

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
                        if (SPRING_ZIPKIN_ENABLED.equals(name)) {
                            boolean productionEnv = Arrays.asList(environment.getActiveProfiles()).contains(BasicProfiles.PRODUCTION);
                            String appEnv = environment.getProperty(ENVIRONMENT_PROPERTY);
                            boolean testOrStage = environmentIsNonProd(appEnv);
                            return productionEnv && !testOrStage;
                        }
                        return null;
                    }
                });
    }

    private boolean environmentIsNonProd(String appEnv) {
        return StringUtils.hasText(appEnv) &&
                (appEnv.toLowerCase().contains("test") || appEnv.toLowerCase().contains("stage") || appEnv.toLowerCase().contains("rbt"));
    }
}
