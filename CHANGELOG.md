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