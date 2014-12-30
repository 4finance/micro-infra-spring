[0.7.3](https://github.com/4finance/micro-infra-spring/tree/0.7.3)
-----
New features:
 * [Issue 107](https://github.com/4finance/micro-infra-spring/issues/107) Every `ServiceRestClient` call should be measured
 * [Issue 105](https://github.com/4finance/micro-infra-spring/issues/105) `@EnableServiceRestClient` alone is useless
 * [Issue 79](https://github.com/4finance/micro-infra-spring/issues/79) Make it possible to draw diagrams of dependencies between services

Bug fixes:
 * [Issue 144](https://github.com/4finance/micro-infra-spring/issues/144) `ResponseRethrowingErrorHandler` fails in getBody() when handling exceptions  bug
 * [Issue 139](https://github.com/4finance/micro-infra-spring/issues/139) `RestServiceClient` from latest micro-infra-spring throws ExecutionException instead of ResponseException
 * [Issue 138](https://github.com/4finance/micro-infra-spring/issues/138) External configuration management should be disabled during tests 

[0.7.2](https://github.com/4finance/micro-infra-spring/tree/0.7.2)
-----
New features:
 * [Issue 5](https://github.com/4finance/micro-infra-spring/issues/5) Add Hystrix
 * [Issue 13](https://github.com/4finance/micro-infra-spring/issues/13) Add automatic retry to fluent RestTemplate calls
 * [Issue 70](https://github.com/4finance/micro-infra-spring/issues/70) More comprehensive dependency management
 * [Issue 96](https://github.com/4finance/micro-infra-spring/issues/96) Disable uptodate plugin on Travis
 * [Issue 99](https://github.com/4finance/micro-infra-spring/issues/99) External properties mechanism should support alternative microservice.json file names
 * [Issue 101](https://github.com/4finance/micro-infra-spring/issues/101) Add easy way to configure JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES
 * [Issue 111](https://github.com/4finance/micro-infra-spring/issues/111) Ensure that we ignore unknown JSON fields
 * [Issue 116](https://github.com/4finance/micro-infra-spring/issues/116) Detection of using Java 8 API
 * [Issue 128](https://github.com/4finance/micro-infra-spring/issues/128) BindException after upgrade of micro-deps-spring-config to 0.5.1

Notable changes:
 * Ability to disable property decryption tests with `-DdisableDecryptionTests=true` flag, see [Troubleshooting](https://github.com/4finance/micro-infra-spring/wiki/Development#troubleshooting)
 * [micro-deps-spring-config](https://github.com/4finance/micro-deps-spring-config/issues?q=milestone%3A0.5.1) upgraded to version `0.5.1`
 * [micro-deps](https://github.com/4finance/micro-deps/issues?q=milestone%3A0.7.1) upgraded to version `0.7.1`
 * [stub-runner-spring](https://github.com/4finance/stub-runner-spring/issues?q=milestone%3A0.2.1) upgraded to version `0.2.1`

[0.7.1](https://github.com/4finance/micro-infra-spring/tree/0.7.1)
-----
Bug fixes:
* [Issue 89](https://github.com/4finance/micro-infra-spring/issues/89) Project definition in pom.xml contains wrong dependency definition (runtime)

New features:
* [Issue 104](https://github.com/4finance/micro-infra-spring/issues/104) Introducing separate module Spring Boot Starter for micro-infra-spring: __micro-infra-spring-boot-starter__
* [Issue 83](https://github.com/4finance/micro-infra-spring/issues/83) Properties management

Notable changes:
* Developers are now required to use Java JCE, see [Development](https://github.com/4finance/micro-infra-spring/wiki/Development)

[0.7.0](https://github.com/4finance/micro-infra-spring/tree/0.7.0)
-----
New features:
* [Issue 72](https://github.com/4finance/micro-infra-spring/issues/72) Add support for header properties in JSON config for a dependency
* [Issue 86](https://github.com/4finance/micro-infra-spring/issues/86)  Make the library spring-boot compliant

Notable changes:
* [micro-deps-spring-config](https://github.com/4finance/micro-deps-spring-config) upgraded to version `0.5.0`
* [stub-runner-spring](https://github.com/4finance/stub-runner-spring) upgraded to version `0.0.2`

[0.6.0](https://github.com/4finance/micro-infra-spring/tree/0.6.0)
-----
New features:
* [Issue 66](https://github.com/4finance/micro-infra-spring/issues/66) SpringBoot anotation style
* [Issue 67](https://github.com/4finance/micro-infra-spring/issues/67) ResponseException should also provide the thrown status (not only body)
* [Issue 69](https://github.com/4finance/micro-infra-spring/issues/69) Let's start releasing with axion or sth else

Bug fixes:
* [Issue 21](https://github.com/4finance/micro-infra-spring/issues/21) /swagger should redirect to /swagger/index.html 
* [Issue 61](https://github.com/4finance/micro-infra-spring/issues/61) When action is triggered by a scheduler CorrelationID is NOT SET

Notable changes:
* [micro-deps-spring-config](https://github.com/4finance/micro-deps-spring-config) upgraded to version `0.4.8`

[0.5.4](https://github.com/4finance/micro-infra-spring/tree/0.5.4)
-----
New features:
* [Issue 42](https://github.com/4finance/micro-infra-spring/issues/42)Create a controller that checks the health of collaborators

Notable changes:
* [micro-deps-spring-config](https://github.com/4finance/micro-deps-spring-config) upgraded to version `0.4.7`

[0.5.3](https://github.com/4finance/micro-infra-spring/tree/0.5.3)
-----
New features:
* micro-infra-spring-test module

[0.5.2](https://github.com/4finance/micro-infra-spring/tree/0.5.2)
-----
Bug fixes:
* Fixed micro-infra-camel module

[0.5.1](https://github.com/4finance/micro-infra-spring/tree/0.5.1)
-----
New features:
* [stub-runner-spring](https://github.com/4finance/stub-runner-spring) added Consumer Driven Contracts

Notable changes:
* [micro-deps-spring-config](https://github.com/4finance/micro-deps-spring-config) upgraded to version `0.4.6`

[0.5.0](https://github.com/4finance/micro-infra-spring/tree/0.5.0)
-----
New features:
* [Issue 18](https://github.com/4finance/micro-infra-spring/issues/18) Add correlationId to headers of a JMS message
* CSV metrics publisher

Notable changes:
* [micro-deps-spring-config](https://github.com/4finance/micro-deps-spring-config) upgraded to version `0.4.4`
* JMX metrics publisher is run also in TEST profile

Breaking changes:
- removed `FilterConfiguration` - import `CorrelationIdConfiguration` and/or `RequestFilterConfiguration` directly when needed
- removed `ControllerExceptionConfiguration` import from all configurations
- removed `RequestFilterConfiguration` import from all configurations
- `RequestFilterConfiguration` renamed to `RequestLoggingConfiguration` and moved to `com.ofg.infrastructure.web.logging` package
- `RequestBodyLoggingContextFilter` moved to `com.ofg.infrastructure.web.logging` package
- `MetricsConfiguration` moved to `com.ofg.infrastructure.metrics.config` package
- `ServiceUnavailableException` moved to `com.ofg.infrastructure.discovery` package
- `CorrelationCallable` moved to `com.ofg.infrastructure.correlationid` package
- `CorrelationIdHolder` moved to `com.ofg.infrastructure.correlationid` package
- `CorrelationIdUpdater` moved to `com.ofg.infrastructure.correlationid` package
- `CorrelationIdAspect` moved to `com.ofg.infrastructure.web.correlationid` package
- `CorrelationIdFilter` moved to `com.ofg.infrastructure.web.correlationid` package
- `CorrelationIdConfiguration` moved to `com.ofg.infrastructure.web.correlationid` package
- `GraphitePublisher.PublishingInterval` inner class moved to separate `PublishingInterval` class

[0.4.3](https://github.com/4finance/micro-infra-spring/tree/0.4.3)
-----
New features:
* micro-infra-camel module with correlation ID interceptor for camel routes

Bug fixes:
* [Issue 59](https://github.com/4finance/micro-infra-spring/issues/59) Possible container lifecycle issues

[0.4.2](https://github.com/4finance/micro-infra-spring/tree/0.4.2)
-----
Bug fixes:
* [Issue 40](https://github.com/4finance/micro-infra-spring/issues/40) Using micro-infra-spring-swagger adds unnecessary dependencies (transitively) to project's runtime classpath

[0.4.1](https://github.com/4finance/micro-infra-spring/tree/0.4.1)
-----
Bug fixes:
* [Issue 39](https://github.com/4finance/micro-infra-spring/issues/39) Version 0.4.0 introduced an exception that broke JSON serialization

[0.4.0](https://github.com/4finance/micro-infra-spring/tree/0.4.0)
-----
New features:
* [Issue 14](https://github.com/4finance/micro-infra-spring/issues/14) Make swagger's UI access microservice's port from properties and not from string

Bug fixes:
* [Issue 30](https://github.com/4finance/micro-infra-spring/issues/30) NoUniqueBeanDefinitionException of type RestOperations

[0.3.0](https://github.com/4finance/micro-infra-spring/tree/0.3.0)
-----
New features:
* [Issue 24](https://github.com/4finance/micro-infra-spring/issues/24) Make correlationId pass when dealing with Spring Reactor

[0.2.2](https://github.com/4finance/micro-infra-spring/tree/0.2.2)
-----
Notable changes:
* [micro-deps-spring-config](https://github.com/4finance/micro-deps-spring-config) upgraded to version `0.4.1`

Bug fixes:
* Issues with looking for an implementation instead of a registered interface related to Metrics

[0.2.1](https://github.com/4finance/micro-infra-spring/tree/0.2.1)
-----
New features:
* [Issue 19](https://github.com/4finance/micro-infra-spring/issues/17) PingController handles now also the HEAD request

[0.2.0](https://github.com/4finance/micro-infra-spring/tree/0.2.0)
-----
New features:
* [Issue 17](https://github.com/4finance/micro-infra-spring/issues/17) metrics path configuration
* [Issue 16](https://github.com/4finance/micro-infra-spring/issues/16) `MetricsConfiguration` configuration

Breaking changes:
* `MetricsRegistryConfiguration` renamed to `MetricsConfiguration`
* `MetricRegistry` bean is in fact our custom implementation called `PathPrependingMetricRegistry`. The way metric name is created has changed.

[0.1.0](https://github.com/4finance/micro-infra-spring/tree/0.1.0)
-----
New features:
* configuration for all request filters and request logging filter  

Notable changes:
* [micro-deps-spring-config](https://github.com/4finance/micro-deps-spring-config) upgraded to version `0.4.0`

Breaking changes:
* REST Template classes moved to `com.ofg.infrastructure.web.resttemplate.custom` package
* `RequestBodyLoggingContextFilter` moved to `com.ofg.infrastructure.web.filter.logging` package
* `SwaggerConfiguration` moved to `com.ofg.infrastructure.web.swagger` package
* `accept(MediaType... acceptableMediaTypes)` method added to `com.ofg.infrastructure.web.resttemplate.fluent.common.response.receive.HeadersSetting` interface

[0.0.9](https://github.com/4finance/micro-infra-spring/tree/0.0.9)
-----
New features:
* widened REST Template abstraction methods parameters' types from `org.springframework.web.client.RestTemplate` class to `org.springframework.web.client.RestOperations` interface

Breaking changes:
* `com.ofg.infrastructure.web.resttemplate.RestTemplateConfiguratio` defines `org.springframework.web.client.RestOperations` instead of `com.ofg.infrastructure.web.resttemplate.RestTemplate`

[0.0.8](https://github.com/4finance/micro-infra-spring/tree/0.0.8)
-----
New features:
* sending multipart requests
* ignoring HTTP requests' responses

Bug fixes:
* fixed correlation ID aspect's pointcut definition

Breaking changes:
* removed `com.ofg.infrastructure.web.filter.CORSFilter`

[0.0.7](https://github.com/4finance/micro-infra-spring/tree/0.0.7)
-----
New features:
* `SwaggerConfiguration,` added to `WebAppConfiguration`
* embedded Swagger resources

[0.0.6](https://github.com/4finance/micro-infra-spring/tree/0.0.6)
-----
New features:
* REST Template

[0.0.5](https://github.com/4finance/micro-infra-spring/tree/0.0.5)
-----
New features:
* ensure that at least one profile is active
* ensure that all active profiles are defined  

[0.0.4](https://github.com/4finance/micro-infra-spring/tree/0.0.4)
-----
New features:
* Graphite publisher

[0.0.3](https://github.com/4finance/micro-infra-spring/tree/0.0.3)
-----
New features:
* metrics registry
* metrics JMX publisher
* `MetricsRegistryConfiguration` and `WebInfrastructureConfiguration` configurations
* `MetricsRegistryConfiguration,` added to `WebAppConfiguration`

[0.0.2](https://github.com/4finance/micro-infra-spring/tree/0.0.2)
-----
Notable changes:
* Java 7 compatibility

[0.0.1](https://github.com/4finance/micro-infra-spring/tree/0.0.1)
-----
Initial release
