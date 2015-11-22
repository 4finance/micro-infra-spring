package com.ofg.infrastructure.discovery;

import com.google.common.collect.ImmutableMap;
import com.ofg.config.BasicProfiles;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
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
@AutoConfigureBefore(SpringCloudZookeeperConfiguration.class)
@ConditionalOnProperty(value = "discovery.properties.enabled", matchIfMissing = true)
public class DiscoveryPropertiesEnabler implements ApplicationContextAware,
        BeanFactoryAware, SmartInitializingSingleton {

    static final String SPRING_ZOOKEEPER_ENABLED = "spring.cloud.zookeeper.enabled";
    static final String SPRING_ZOOKEEPER_DISCOVERY_ENABLED = "spring.cloud.zookeeper.discovery.enabled";
    static final String SPRING_RIBBON_ZOOKEEPER_ENABLED = "ribbon.zookeeper.enabled";
    static final String SPRING_AUTOCONFIGURE_EXCLUDE = "spring.autoconfigure.exclude";
    static final String SPRING_AUTOCONFIGURE_DEFAULT_EXCLUDES = "org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration";

    static final ImmutableMap<String, String> DEFAULTS_FOR_NON_SPRING_CLOUD =
            new ImmutableMap.Builder<String, String>()
                    .put(SPRING_ZOOKEEPER_ENABLED, "false")
                    .put(SPRING_ZOOKEEPER_DISCOVERY_ENABLED, "false")
                    .put(SPRING_RIBBON_ZOOKEEPER_ENABLED, "false")
                    .put(SPRING_AUTOCONFIGURE_EXCLUDE, SPRING_AUTOCONFIGURE_DEFAULT_EXCLUDES)
                    .build();

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
                new PropertySource<Object>("Spring Cloud Zookeeper Enabling") {
                    @Override
                    public Object getProperty(String name) {
                        String result = DEFAULTS_FOR_NON_SPRING_CLOUD.get(name);
                        if (StringUtils.hasText(result) && !springCloudProfileIsActive(environment)) {
                            return result;
                        }
                        return null;
                    }
                });
    }

    private boolean springCloudProfileIsActive(ConfigurableEnvironment environment) {
        return Arrays.asList(environment.getActiveProfiles()).contains(BasicProfiles.SPRING_CLOUD);
    }
}
