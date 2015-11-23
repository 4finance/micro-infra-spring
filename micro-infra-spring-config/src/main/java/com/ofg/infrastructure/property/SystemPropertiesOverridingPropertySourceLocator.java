package com.ofg.infrastructure.property;

import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Collections;

@Configuration
public class SystemPropertiesOverridingPropertySourceLocator implements PropertySourceLocator {
    @Override
    public PropertySource<?> locate(Environment environment) {
        return new MapPropertySource("microInfraSpringCloudConfigSource",
                Collections.<String, Object>singletonMap("spring.cloud.config.overrideSystemProperties", "false"));
    }
}
