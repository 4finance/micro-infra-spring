[![Build Status](https://travis-ci.org/4finance/micro-infra-spring.svg?branch=master)](https://travis-ci.org/4finance/micro-infra-spring) [![Coverage Status](http://img.shields.io/coveralls/4finance/micro-infra-spring/master.svg)](https://coveralls.io/r/4finance/micro-infra-spring) [ ![Download](https://api.bintray.com/packages/4finance/micro/micro-infra-spring/images/download.png) ](https://bintray.com/4finance/micro/micro-infra-spring/_latestVersion)

micro-infra-spring
=======================
Sets up the whole Spring infrastructure stack that will turn your microservice into a beauty. All code examples presented below are in Groovy.
 
It consists of several different domains (most likely we will modularize it in the future):

- Service discovery
- Spring environment setup
- Health check
- Metrics publishing
- Swagger (API documentation)
- Controller exceptions handling
- CorrelationId setting
- Request body logging
- Abstraction over RestTemplate (bound with service discovery)

##How to use all of it?

If you want to just profit from the whole stack presented above either

component scan over __com.ofg.infrastructure__:

```
@Configuration
@ComponentScan("com.ofg.infrastructure")
class MyWebAppConfiguration {
}
```

or add the configuration explicitly

```
@Configuration
@Import(com.ofg.infrastructure.config.WebAppConfiguration)
class MyWebAppConfiguration {
}
```

##How to use only parts of it?

If you want to just profit only from the selected modules (if that's actually possible) either

component scan over __com.ofg.infrastructure.(selected_module)__:

```
@Configuration
@ComponentScan("com.ofg.infrastructure.metrics")
class MyWebAppConfiguration {
}
```

or add the configuration explicitly

```
@Configuration
@Import(com.ofg.infrastructure.metrics.registry.MetricsRegistryConfiguration)
class MyMetricsRegistryConfiguration {
}
```

##Modules

Let' move over those modules in more depth

## Service discovery

### Description

This project reuses the beans and configurations set up in the [micro-deps-spring-config](https://github.com/4finance/micro-deps-spring-config) project. Please check the documentation of that project to receive more in depth information about its content.

To put it briefly we are setting up via __ServiceDiscoveryConfiguration__ that imports __ServiceResolverConfiguration__ configurations and beans that provide (names in brackets are names of classes)

- Microservice's host and port (__MicroserviceAddressProvider__)
- Zookeeper connection client (__CuratorFramework__)
- Registration of the microservice in Zookeeper (__ServiceInstance__)
- Service discovery (__ServiceDiscovery__)
- Dependency watching - checking if dependency is still alive (__DependencyWatcher__)
- Parsing of microservice configuration - defaults to classpath resource _microservice.json_ (__ServiceConfigurationResolver__)
- Service resolution (__ServiceResolver__)

### Module configuration

If you want to setup only this module you have to either

component scan over __com.ofg.infrastructure.discovery__:

```
@Configuration
@ComponentScan("com.ofg.infrastructure.discovery")
class MyWebAppConfiguration {
}
```

or add the configuration explicitly

```
@Configuration
@Import(com.ofg.infrastructure.discovery.ServiceDiscoveryConfiguration)
class MyModuleConfiguration {
}
```

## Spring Environment setup

### Description

__EnvironmentSetupVerifier__ is a Spring's _ApplicationListener_ that verifies that you have provided a spring profile upon application execution (via __spring.profiles.active__ system property). If it's not provided the application will close with error.


### Example of usage

Example setup for Groovy (note that you don't have to register __EnvironmentSetupVerifier__ as a bean):

```
@TypeChecked
@Configuration
@EnableAutoConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = ["com.ofg.microservice", "com.ofg.twitter", "com.mangofactory.swagger"])
@EnableCaching
@EnableAsync
class Application {

    static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application)
        application.addListeners(new EnvironmentSetupVerifier(Profiles.all()))
        application.run(args)
    }
}
```

where __Profiles__ looks like this:

```
@TypeChecked
class Profiles {
    public static final String PRODUCTION = "prod"
    public static final String TEST = "test"
    public static final String DEVELOPMENT = "dev"

    static List<String> all() {
        return [PRODUCTION, TEST, DEVELOPMENT]
    }
}
``` 

## Health check

### Description

It's nice to know that your application is up and running, isn't it? We monitor our microservices via Zabbix that pings to our controllers. If you want to you can do the same by picking this module.

In __HealthCheckConfiguration__ we are registering a __PingController__ that if you send a __GET__ request to __/ping__ it will respond with __OK__ if it's alive.


### Module configuration

If you want to setup only this module you have to either

component scan over __com.ofg.infrastructure.healthcheck__:

```
@Configuration
@ComponentScan("com.ofg.infrastructure.healthcheck")
class MyWebAppConfiguration {
}
```

or add the configuration explicitly

```
@Configuration
@Import(com.ofg.infrastructure.healthcheck.HealthCheckConfiguration)
class MyModuleConfiguration {
}
```

## Metrics publishing

### Description

We have microservices so we need to monitor our system. Not only whether they are alive or not. We want to measure as much as possible. From every possible angle - amount of requests, technical stuff like how many errors we have or from business perspective - how many loans are issued per minute (or whatever). We collect those measurements in [Graffite](http://graphite.wikidot.com).

If you select this modul we will give you two publishers (Graphite - __GraphitePublisher__ and JMX - __JmxPublisher__) that will provide setup for your metrics to be published in Graphite and as an MBean. 

As a Spring bean we are providing __MetricsRegistry__ that each of the metrics reporter needs to send upload data to its recipient.

### Example of usage

Below you can find an example of Groovy configuration of metrics registry (extract from [Bootmicroservice template](https://github.com/4finance/boot-microservice/blob/master/src/main/groovy/com/ofg/microservice/config/MetricsPublishersConfiguration.groovy))
```
      @Configuration
      @Import(MetricsRegistryConfiguration)
      @Profile(Profiles.PRODUCTION)
      public class GraphitePublisherConfigration {
           @Autowired MetricRegistry metricsRegistry;
 
           @Bean
           Graphite graphite(@Value("${graphite.host:graphite.4finance.net}") String hostname, @Value("${graphite.port:2003}") int port) {
                return new Graphite(new InetSocketAddress(hostname, port));
           }
 
           @Bean(initMethod = "start", destroyMethod = "stop")
           GraphitePublisher graphitePublisher(Graphite graphite, MetricRegistry metricRegistry) {
           PublishingInterval publishingInterval = new PublishingInterval(15, SECONDS);
                return new GraphitePublisher(graphite, publishingInterval, metricRegistry, MINUTES, MILLISECONDS);
           }
      }
```

### Module configuration

If you want to setup only this module you have to either

component scan over __com.ofg.infrastructure.metrics.publishing__:

```
@Configuration
@ComponentScan("com.ofg.infrastructure.metrics")
class MyWebAppConfiguration {
}
```

or add the configuration explicitly

```
@Configuration
@Import(com.ofg.infrastructure.healthcheck.MetricsRegistryConfiguration)
class MyModuleConfiguration {
}
```

## Swagger (API documentation)

### Description

Imagine that you are entering to a new project and would like to check how does the outside world communicate with your Spring application. How would you do it? How would you test your application? You can search your code for @Controller annotated methods but we don't like to do lame stuff - we are too cool to do that.

That's why we use [Swagger](https://github.com/wordnik/swagger-spec). And you should too - check out their [live demo](http://petstore.swagger.wordnik.com/). It's documenting your API automatically but you can provide more annotations to describe your API even more beautifully.

#### Backend API documentation and Swagger-UI
Swagger consists of two separate parts - __documenting API (backend)__ and __presentation of the API (swagger-ui)__. The configuraiton presented below shows you how to add the __backend API documentation__. We already provide Swagger-UI for you cause it's placed in __/resources/static/swagger__ folder. That means that you will have it (for Spring Boot) out of the box when you access the http://yourmicroservice.com/swagger/ URL.

### Example of usage

Below you can find an example of some of Swagger's annotations in action.

```
package com.ofg.infrastructure.healthcheck

import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * {@link RestController} that responds with OK when server is alive
 */
@RestController
@CompileStatic
@PackageScope
@Api(value = "ping", description = "PING API")
class PingController {

    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Ping server", notes = "Returns OK if server is alive")
    String ping() {
        return "OK"
    }
}

```

#### Overriding default Swagger-UI settings

When using Swagger-UI you have to provide the URL to which you want to call from your front-end to retrieve the API docs. Until now (05/09/2014) the value where the docs are searched for is fixed. So either you have to provide it yourself manualy or you can create a resource that will be picked up by your resource resolvers (for Spring Boot for example in __/resources/static__ folder)

```
swagger/swagger-config.js
```

there you can provide custom config for Swagger - for example:

```
window.authorizations.add("key", new ApiKeyAuthorization("someHeaderKey", "someHeaderValue", "header"));
```

### Module configuration

If you want to setup only this module  you have to either

component scan over __com.ofg.infrastructure.web.swagger__:

```
@Configuration
@ComponentScan("com.ofg.infrastructure.web.swagger")
class MyWebAppConfiguration {
}
```

or add the configuration explicitly

```
@Configuration
@Import(com.ofg.infrastructure.web.swagger.SwaggerConfiguration)
class MyModuleConfiguration {
}
```

## Controller exceptions handling / JSON View resolving

### Description

Sometimes exceptions roam around like crazy in your system and might forget to catch them. Not to worry - we will come to the rescue! Our __ControllerExceptionHandler__ catches all exceptions there are and will present them in a nice JSON format (assuming that you use our __JsonViewResolver__). 

You are a good programmer so most likely you are using some JSR validation implementations. That's great and we'll be happy to present those errors in a nice way.

### Example of usage

Let's assume that we have a following controller

```
@RestController
class TestController {
    @RequestMapping(value = "/test", produces = "application/json", method = RequestMethod.POST)
    String test(@RequestBody @Valid TestRequest request, BindingResult result) {
        checkIfResultHasErrors(result)
        return "OK"
    }

    private void checkIfResultHasErrors(BindingResult result) {
        if (result.hasErrors()) {
            throw new BadParametersException(result.getAllErrors())
        }
    }
}

class TestRequest {
    @AssertTrue
    boolean shouldBeTrue
}

```

If validation fails we throw __BadParametersException__ that we catch in __ControllerExceptionHandler__ and using __JsonViewResolver__ we can pretty print that JSON for you!

### Module configuration

#### Controller exceptions handling 
If you want to setup only this module you have to either

component scan over __com.ofg.infrastructure.web.exception__:

```
@Configuration
@ComponentScan("com.ofg.infrastructure.web.exception")
class MyWebAppConfiguration {
}
```

or add the configuration explicitly

```
@Configuration
@Import(com.ofg.infrastructure.web.exception.ControllerExceptionConfiguration)
class MyModuleConfiguration {
}
```

#### JSON View resolving
If you want to setup only this module you have to either

component scan over __com.ofg.infrastructure.web.view__:

```
@Configuration
@ComponentScan("com.ofg.infrastructure.web.view")
class MyWebAppConfiguration {
}
```

or add the configuration explicitly

```
@Configuration
@Import(com.ofg.infrastructure.web.view.ViewConfiguration)
class MyModuleConfiguration {
}
```

## CorrelationId setting

### Description

We are working with microservices. Many microservices. Imagine a series of 20 microservices processing one request - let's say that we want to grant a loan to a Mr Smith. Since we are profesionals we have log collecting tools like [logstash](http://logstash.net/) and [kibana](http://www.elasticsearch.org/overview/kibana/). Now imagine that something broke - an exception occurred. How can you find in those hundreds of lines of logs which ones are related to Mr Smith's case? Correlation id will speed up your work effortlessly.

Since we are using Spring then most likely we can receive or send a request by
- a __@Controller__ annotated controller
- a __@RestController__ annotated controller
- by sending a request via a __RestTemplate__

To deal with all of those approaches we:
- created a filter __CorrelationIdFilter__ that will set a correlation id header named __correlationId__ on your request
- created an aspect __CorrelationIdAspect__ that makes it possible to work with Servlet 3.0 async feature (you have to have a controller method that returns a __Callable__)
- the very same aspect allows checks if you are using a __RestOperations__- base interface of __RestTemplate__. If that is the case then we are setting the __correlationId__ header on the request that you are sending (via __exchange__ method).

### Module configuration

If you want to setup only this module you have to either

component scan over __com.ofg.infrastructure.web.filter.correlationid__:

```
@Configuration
@ComponentScan("com.ofg.infrastructure.web.filter.correlationid")
class MyWebAppConfiguration {
}
```

or add the configuration explicitly

```
@Configuration
@Import(com.ofg.infrastructure.web.filter.correlationid.CorrelationIdConfiguration)
class MyModuleConfiguration {
}
```

## Request body logging

### Description

This module is responsible for logging request body in debug mode. It registers a __Log4jNestedDiagnosticContextFilter__ extension called __RequestBodyLoggingContextFilter__. 

### Example of usage

You can provide max payload that should be printed in logs by providing a property like presented below (example for a payload of 1000 chars).

```
request.payload.logging.maxlength:1000
```

### Module configuration

If you want to setup only this module you have to either

component scan over __com.ofg.infrastructure.web.filter.logging__:

```
@Configuration
@ComponentScan("com.ofg.infrastructure.web.filter")
class MyWebAppConfiguration {
}
```

or add the configuration explicitly

```
@Configuration
@Import(com.ofg.infrastructure.web.filter.logging.RequestFilterConfiguration)
class MyModuleConfiguration {
}
```
