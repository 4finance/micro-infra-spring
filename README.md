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

Default Micro-deps Spring test configuration

## Usage

### Spock Integration test

Just extend the __IntegrationSpec__ specification and you're ready to go!

```groovy
class AcceptanceSpec extends IntegrationSpec {

}
```

That way you'll have:

* 'test' profile activated
* Spring's MVC web app activated

### MVC Spock Integration test

Just extend the __MvcIntegrationSpec__ specification and you're ready to go!

```groovy
class AcceptanceSpec extends MvcIntegrationSpec {

}
```

That way you'll have:

* 'test' profile activated
* Spring's MVC web app activated
* access to __StubbedServiceResolver__ to control addresses of your collaborators (via service discovery)
* access to __ApplicationContext__ and __WebApplicationContext__
* access to __MockMvc__

### MVC Spock Integration test with Wiremock

Just extend the __MvcWiremockIntegrationSpec__ specification and you're ready to go!

```groovy
class AcceptanceSpec extends MvcWiremockIntegrationSpec {

}
```

That way you'll have:

* 'test' profile activated
* Spring's MVC web app activated
* access to __StubbedServiceResolver__ to control addresses of your collaborators (via service discovery)
* access to __ApplicationContext__ and __WebApplicationContext__
* access to __MockMvc__
* access to __stubInteraction()__ method that allows you to stub __WireMock__. By default all of your collaborators
 are pointing to microservice.host:microservice.port/COLLABORATOR_NAME_IN_MICROSERVICE_METADATA . So if you need to
 you can control your collaborators' responses
