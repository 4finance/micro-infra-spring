package com.ofg.infrastructure.autoconfigure;

import com.ofg.infrastructure.healthcheck.EnableHealthCheck;
import com.ofg.infrastructure.metrics.config.EnableMetrics;
import com.ofg.infrastructure.tracing.EnableTracing;
import com.ofg.infrastructure.web.correlationid.EnableCorrelationId;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClientConfiguration;
import com.ofg.infrastructure.web.view.ViewConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableHealthCheck
@EnableMetrics
@EnableCorrelationId
@EnableTracing
@Import({ServiceRestClientConfiguration.class,  ViewConfiguration.class})
public class SpringCloudMicroSetup {
}
