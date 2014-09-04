[![Build Status](https://travis-ci.org/4finance/micro-infra-spring.svg?branch=master)](https://travis-ci.org/4finance/micro-infra-spring) [![Coverage Status](http://img.shields.io/coveralls/4finance/micro-infra-spring/master.svg)](https://coveralls.io/r/4finance/micro-infra-spring) [ ![Download](https://api.bintray.com/packages/4finance/micro/micro-infra-spring/images/download.png) ](https://bintray.com/4finance/micro/micro-infra-spring/_latestVersion)

micro-infra-spring
=======================
Sets up the whole Spring infrastructure stack that will turn your microservice into a beauty.
 
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
@Import(com.ofg.infrastructure.config.WebAppConfiguration.class)
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
@Import(com.ofg.infrastructure.metrics.registry.MetricsRegistryConfiguration.class)
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
@Import(com.ofg.infrastructure.discovery.ServiceDiscoveryConfiguration.class)
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
@Import(com.ofg.infrastructure.healthcheck.HealthCheckConfiguration.class)
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
      @Import(MetricsRegistryConfiguration.class)
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
@Import(com.ofg.infrastructure.healthcheck.MetricsRegistryConfiguration.class)
class MyModuleConfiguration {
}
```

## Swagger (API documentation)

### Description

Imagine that you are entering to a new project and would like to check how does the outside world communicate with your Spring application. How would you do it? How would you test your application? You can search your code for @Controller annotated methods but we don't like to do lame stuff - we are too cool to do that.

That's why we use [Swagger](https://github.com/wordnik/swagger-spec). And you should too - check out their [live demo](http://petstore.swagger.wordnik.com/). It's documenting your API automatically but you can provide more annotations to describe your API even more beautifully.

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

### Module configuration

If you want to setup only this module you have to either

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
@Import(com.ofg.infrastructure.web.swagger.SwaggerConfiguration.class)
class MyModuleConfiguration {
}
```
