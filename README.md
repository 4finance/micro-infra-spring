[![Build Status](https://travis-ci.org/4finance/micro-deps-spring-config.svg?branch=master)](https://travis-ci.org/4finance/micro-deps-spring-config) 
[![Coverage Status](http://img.shields.io/coveralls/4finance/micro-deps-spring-config/master.svg)](https://coveralls.io/r/4finance/micro-deps-spring-config)
[ ![Download](https://api.bintray.com/packages/4finance/micro-deps/micro-deps-spring-config/images/download.svg) ](https://bintray.com/4finance/micro-deps/micro-deps-spring-config/_latestVersion)

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

You can further configure micro-deps with the following properties (we present description and default values):

```
# microservice metadata file
microservice.config.file=classpath:microservice.json

# microservice host
microservice.host=InetAddress.getLocalHost().getHostAddress()

# microservice port
microservice.port=if not configured then 'port' system property or 'server.port' environment variable is used (in the mentioned order) and if that fails defaults to port '8080'

# service resolver URL
service.resolver.url=localhost:2181

# number of connection retries
service.resolver.connection.retry.times=5

# wait time in milliseconds between consecutive connection retries
service.resolver.connection.retry.wait=1000
```

Take a look at [stub-runner-spring project](https://github.com/4finance/stub-runner-spring/wiki/How-to-use-it) for more information on our [Consumer Driven Contracts](http://martinfowler.com/articles/consumerDrivenContracts.html) implementation. Below you can find the properties that you can set in that regard together with default values:

```
# minPortValue min port value of the Wiremock instance for the given collaborator
stubrunner.port.range.min=10000

# maxPortValue max port value of the Wiremock instance for the given collaborator
stubrunner.port.range.max=15000

# stubRepositoryRoot root URL from where the JAR with stub mappings will be downloaded
stubrunner.stubs.repository.root=http://nexus.4finance.net/content/repositories/Pipeline

# stubsGroup group name of the dependency containing stub mappings
stubrunner.stubs.group=com.ofg

# stubsModule module name of the dependency containing stub mappings
stubrunner.stubs.module=stub-definitions

```

Micro-deps-spring-test-config  [ ![Download](https://api.bintray.com/packages/4finance/micro-deps/micro-deps-spring-test-config/images/download.svg) ](https://bintray.com/4finance/micro-deps/micro-deps-spring-test-config/_latestVersion)
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
* access to application context
* access to web application context

### MVC integration tests with [WireMock](http://wiremock.org/)

Just extend the __MvcWiremockIntegrationSpec__ specification and you're ready to go!

```groovy
class AcceptanceSpec extends MvcWiremockIntegrationSpec {

}
```
## JUnit specifications' base classes (since 0.4.3)

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
* access to application context
* access to web application context
* access to __stubInteraction()__ method that allows you to stub __WireMock__.
