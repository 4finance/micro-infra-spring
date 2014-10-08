0.4.3
-----
New features:
* micro-infra-camel module with correlation ID interceptor for camel routes

Bug fixes:
* [Issue 59](https://github.com/4finance/micro-infra-spring/issues/59) Possible container lifecycle issues

0.4.2
-----
Bug fixes:
* [Issue 40](https://github.com/4finance/micro-infra-spring/issues/40) Using micro-infra-spring-swagger adds unnecessary dependencies (transitively) to project's runtime classpath

0.4.1
-----
Bug fixes:
* [Issue 39](https://github.com/4finance/micro-infra-spring/issues/39) Version 0.4.0 introduced an exception that broke JSON serialization

0.4.0
-----
New features:
* [Issue 14](https://github.com/4finance/micro-infra-spring/issues/14) Make swagger's UI access microservice's port from properties and not from string

Bug fixes:
* [Issue 30](https://github.com/4finance/micro-infra-spring/issues/30) NoUniqueBeanDefinitionException of type RestOperations

0.3.0
-----
New features:
* [Issue 24](https://github.com/4finance/micro-infra-spring/issues/24) Make correlationId pass when dealing with Spring Reactor

0.2.2
-----
Bug fixes:
* Issues with looking for an implementation instead of a registered interface related to Metrics

0.2.1
-----
New features:
* [Issue 19](https://github.com/4finance/micro-infra-spring/issues/17) PingController handles now also the HEAD request

0.2.0
-----
New features:ServiceRestClientConfiguration
* [Issue 17](https://github.com/4finance/micro-infra-spring/issues/17) Moved metrics path generation from 'bluecash-adapter' to micro-infra-spring
* [Issue 16](https://github.com/4finance/micro-infra-spring/issues/16) Copied MetricsPublishingConfiguration in BootMicroservice to micro-infra-spring

Breaking changes:
* `MetricsRegistryConfiguration` renamed to `MetricsConfiguration`
* `MetricRegistry` bean is in fact our custom implementation called `PathPrependingMetricRegistry`. The way metric name is created has changed.

0.1.0
-----
New features:
* [micro-deps-spring-config](https://github.com/4finance/micro-deps) upgraded to version `0.4.0`
* configuration for all request filters and request logging filter  

Breaking changes:
* REST Template classes moved to `com.ofg.infrastructure.web.resttemplate.custom` package
* `RequestBodyLoggingContextFilter` moved to `com.ofg.infrastructure.web.filter.logging` package
* `SwaggerConfiguration` moved to `com.ofg.infrastructure.web.swagger` package
* `accept(MediaType... acceptableMediaTypes)` method added to `com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersSetting` interface

0.0.9
-----
New features:
* widened REST Template abstraction methods parameters' types from `org.springframework.web.client.RestTemplate` class to `org.springframework.web.client.RestOperations` interface

Breaking changes:
* `com.ofg.infrastructure.web.resttemplate.RestTemplateConfiguratio` defines `org.springframework.web.client.RestOperations` instead of `com.ofg.infrastructure.web.resttemplate.RestTemplate`

0.0.8
-----
New features:
* sending multipart requests
* ignoring HTTP requests' responses

Bug fixes:
* fixed correlation ID aspect's pointcut definition

Breaking changes:
* removed `com.ofg.infrastructure.web.filter.CORSFilter`

0.0.7
-----
New features:
* `SwaggerConfiguration,` added to `WebAppConfiguration`
* embedded Swagger resources

0.0.6
-----
New features:
* REST Template

0.0.5
-----
New features:
* ensure that at least one profile is active
* ensure that all active profiles are defined  

0.0.4
-----
New features:
* Graphite publisher

0.0.3
-----
New features:
* metrics registry
* metrics JMX publisher
* `MetricsRegistryConfiguration` and `WebInfrastructureConfiguration` configurations
* `MetricsRegistryConfiguration,` added to `WebAppConfiguration`

0.0.2
-----
New features:
* Java 7 compatibility

0.0.1
-----
Initial release
