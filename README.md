[![Build Status](https://travis-ci.org/4finance/micro-deps-spring-config.svg?branch=master)](https://travis-ci.org/4finance/micro-deps-spring-config) 
[![Coverage Status](http://img.shields.io/coveralls/4finance/micro-deps-spring-config/master.svg)](https://coveralls.io/r/4finance/micro-deps-spring-config)

[ ![Download](https://api.bintray.com/packages/4finance/micro-deps/micro-deps-spring-config/images/download.png) ](https://bintray.com/4finance/micro-deps/micro-deps-spring-config/_latestVersion) 
Micro-deps-spring-config
=================

Default Micro-deps Spring configuration

##Usage

Nothing simpler, just import `com.ofg.infrastructure.discovery.ServiceResolverConfiguration` configuration class:

```java
@Configuration
@Import(ServiceResolverConfiguration.class)
class YourApplicationConfiguration {...}
```

## Configuration

You can further configure micro-deps with the following properties:
* microservice.config.file - microservice metadata file,  defaults to `classpath:microservice.json`
* microservice.host - microservice host, defaults to `InetAddress.getLocalHost().getHostAddress()`
* microservice.port - microservice port, if not configured then `port` system property or `server.port` environment variable is used (in the mentioned order) and if that fails defaults to port `8080`
* service.resolver.url - service resolver URL, defaults to `localhost:2181`
* service.resolver.connection.retry.times - number of connection retries, defaults to `5`
* service.resolver.connection.retry.wait - wait time in milliseconds between consecutive connection retries, defaults to `1000`

[ ![Download](https://api.bintray.com/packages/4finance/micro-deps/micro-deps-spring-test-config/images/download.png) ](https://bintray.com/4finance/micro-deps/micro-deps-spring-test-config/_latestVersion)
Micro-deps-spring-test-config
=================

Default Micro-deps Spring test configuration - both for JUnit and Spock

## Spock specifications' base classes

### Integration tests

Just extend the __IntegrationSpec__ specification and you're ready to go!

```groovy
class AcceptanceSpec extends IntegrationSpec {

}
```

That way you'll have:

* 'test' profile activated
* __org.springframework.web.context.WebApplicationContext__ loaded

### MVC integration tests

Just extend the __MvcIntegrationSpec__ specification and you're ready to go!

```groovy
class AcceptanceSpec extends MvcIntegrationSpec {

}
```

That way you'll have:

* 'test' profile activated
* __org.springframework.web.context.WebApplicationContext__ loaded
* Spring MVC test support enabled
* access to stubbed service resolver
* access to application context
* access to web application context

### MVC integration tests with [WireMock](http://wiremock.org/)

Just extend the __MvcWiremockIntegrationSpec__ specification and you're ready to go!

```groovy
class AcceptanceSpec extends MvcWiremockIntegrationSpec {

}
```
## JUnit specifications' base classes

### Integration tests

Just extend the __IntegrationTest__ class and you're ready to go!

```groovy
class AcceptanceTest extends IntegrationTest {

}
```

That way you'll have:

* 'test' profile activated
* __org.springframework.web.context.WebApplicationContext__ loaded

### MVC integration tests

Just extend the __MvcIntegrationTest__ class and you're ready to go!

```groovy
class AcceptanceTest extends MvcIntegrationTest {

}
```

That way you'll have:

* 'test' profile activated
* __org.springframework.web.context.WebApplicationContext__ loaded
* Spring MVC test support enabled
* access to stubbed service resolver
* access to application context
* access to web application context

### MVC integration tests with [WireMock](http://wiremock.org/)

Just extend the __MvcWiremockIntegrationTest__ class and you're ready to go!

```groovy
class AcceptanceTest extends MvcWiremockIntegrationTest {

}
``

## Why?

That way you'll have:

* 'test' profile activated
* __org.springframework.web.context.WebApplicationContext__ loaded
* Spring MVC test support enabled
* __WireMock__ server running
* access to stubbed service resolver
* access to application context
* access to web application context
* access to __stubInteraction()__ method that allows you to stub __WireMock__. By default all of your collaborators
 are pointing to microservice.host:microservice.port/COLLABORATOR_NAME_IN_MICROSERVICE_METADATA . So if you need to
 you can control your collaborators' responses
