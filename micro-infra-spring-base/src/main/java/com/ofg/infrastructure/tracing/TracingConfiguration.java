package com.ofg.infrastructure.tracing;

import com.ofg.config.BasicProfiles;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.sleuth.zipkin.ZipkinAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(BasicProfiles.SPRING_CLOUD)
@AutoConfigureBefore(ZipkinAutoConfiguration.class)
public class TracingConfiguration { }
