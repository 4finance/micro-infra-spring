package com.ofg.infrastructure.tracing;

import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.cloud.sleuth.instrument.scheduling.TraceSchedulingAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ TraceAutoConfiguration.class, TraceSchedulingAutoConfiguration.class })
public class TracingConfiguration {

}
