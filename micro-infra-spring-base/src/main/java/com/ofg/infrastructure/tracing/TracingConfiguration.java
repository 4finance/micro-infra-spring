package com.ofg.infrastructure.tracing;

import com.ofg.infrastructure.scheduling.TaskSchedulingConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(TaskSchedulingConfiguration.class)
public class TracingConfiguration {
}
