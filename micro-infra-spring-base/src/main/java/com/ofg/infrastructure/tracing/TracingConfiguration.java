package com.ofg.infrastructure.tracing;

import com.ofg.infrastructure.scheduling.TaskSchedulingConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration;
import org.springframework.cloud.sleuth.log.SleuthLogAutoConfiguration;
import org.springframework.cloud.sleuth.zipkin.ZipkinAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(TaskSchedulingConfiguration.class)
@AutoConfigureBefore({ ZipkinAutoConfiguration.class, TraceAutoConfiguration.class, SleuthLogAutoConfiguration.class })
public class TracingConfiguration {
}
