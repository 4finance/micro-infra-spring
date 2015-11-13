[![Build Status](https://travis-ci.org/4finance/micro-infra-spring.svg?branch=master)](https://travis-ci.org/4finance/micro-infra-spring) [![Coverage Status](http://img.shields.io/coveralls/4finance/micro-infra-spring/master.svg)](https://coveralls.io/r/4finance/micro-infra-spring)[ ![Download](https://api.bintray.com/packages/4finance/micro/micro-infra-spring/images/download.svg) ](https://bintray.com/4finance/micro/micro-infra-spring/_latestVersion)
[![Stack Overflow](https://img.shields.io/badge/stack%20overflow-micro%20infra%20spring-4183C4.svg)](https://stackoverflow.com/questions/tagged/micro-infra-spring)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/4finance/micro-infra-spring?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

micro-infra-spring
======================

Sets up the whole Spring infrastructure stack that will turn your microservice into a beauty.

Check out [the wiki](https://github.com/4finance/micro-infra-spring/wiki) to have a deeper insight into the project.

# TROUBLESHOOTING SPRING CLOUD INTEGRATION

Check [boot-microservice](https://github.com/4finance/boot-microservice) for an example of a working application

Check [spring-cloud-zookeeper documentation](https://github.com/spring-cloud/spring-cloud-zookeeper/blob/master/docs/src/main/asciidoc/spring-cloud-zookeeper.adoc)
on how to register your application with properties in Zookeeper

## My application doesn't seem to use SpringCloud capabilities

Ensure that you have ALSO passed the `springCloud` profile. For example:

```
./gradlew bootRun -Dspring.profiles.active=prod,springCloud -DAPP_ENV=prod -DCONFIG_FOLDER=/properties -Dserver.port=9090 -Dspring.application.name=foo
```
 
## Wrong version of spring boot

### Issue
 
You may find issues like `NoClassDefFoundError` or `ArrayStoreException`. That most likely means that you have different versions of Spring on your classpath.
 
Example of an exception:

```
Exception in thread "main" java.lang.NoClassDefFoundError: org/springframework/boot/context/config/ConfigFileEnvironmentPostProcessor
	at org.springframework.cloud.config.server.NativeEnvironmentRepository.findOne(NativeEnvironmentRepository.java:99)
	at com.ofg.infrastructure.property.FileSystemLocator.locate(FileSystemLocator.java:41)
	at org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration.initialize(PropertySourceBootstrapConfiguration.java:80)
	at org.springframework.boot.SpringApplication.applyInitializers(SpringApplication.java:567)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:303)
	at pl.uservices.dojrzewatr.Application.main(Application.java:18)
Caused by: java.lang.ClassNotFoundException: org.springframework.boot.context.config.ConfigFileEnvironmentPostProcessor
	at java.net.URLClassLoader.findClass(URLClassLoader.java:381)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:424)
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:331)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:357)
	... 6 more
```

### Workaround

Enforce Spring and Spring Boot library versions

```
configurations {
    all {
        resolutionStrategy {
            eachDependency { DependencyResolveDetails details ->
                if (details.requested.group == 'org.springframework.boot') { details.useVersion '1.3.0.RC1' }
                if (details.requested.group == 'org.springframework') { details.useVersion '4.2.2.RELEASE' }
            }
        }
    }
}
```

## ClientHttpRequestFactory duplicated beans

### Issue
 
With Ribbon coming in you have another `ClientHttpRequestFactory`. If you autowire one in your codebase you'll get the following exception:
 
```
Caused by: org.springframework.beans.factory.BeanCreationException: Could not autowire field: org.springframework.http.client.ClientHttpRequestFactory pl.uservices.dojrzewatr.brewing.BrewConfiguration.clientHttpRequestFactory; nested exception is org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type [org.springframework.http.client.ClientHttpRequestFactory] is defined: expected single matching bean but found 2: requestFactory,ribbonClientHttpRequestFactory
	at org.springrk.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement.inject(AutowiredAnnotationBeanPostProcessor.java:571) ~[spring-beans-4.2.2.RELEASE.jar:4.2.2.RELEASE]
	at org.springframework.beans.factory.annotation.InjectionMetadata.inject(InjectionMetadata.java:88) ~[spring-beans-4.2.2.RELEASE.jar:4.2.2.RELEASE]
	at org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.postProcessPropertyValues(AutowiredAnnotationBeanPostProcessor.java:331) ~[spring-beans-4.2.2.RELEASE.jar:4.2.2.RELEASE]
	... 38 common frames omitted
Caused by: org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type [org.springframework.http.client.ClientHttpRequestFactory] is defined: expected single matching bean but found 2: requestFactory,ribbonClientHttpRequestFactory
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1126) ~[spring-beans-4.2.2.RELEASE.jar:4.2.2.RELEASE]
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1014) ~[spring-beans-4.2.2.RELEASE.jar:4.2.2.RELEASE]
	at org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement.inject(AutowiredAnnotationBeanPostProcessor.java:543) ~[spring-beans-4.2.2.RELEASE.jar:4.2.2.RELEASE]
	... 40 common frames omitted
```

### Workaround

Pass the `@Qualifier("requestFactory")` to get one from micro-infra

```
@Autowired @Qualifier("requestFactory") ClientHttpRequestFactory clientHttpRequestFactory;
```

## ServiceConfigurationResolver missing

### Issue

If you're using `springCloud` profile you might get the following exception
 
```
Unsatisfied dependency expressed through ... of type [com.ofg.infrastructure.discovery.ServiceConfigurationResolver]: : 
No qualifying bean of type [com.ofg.infrastructure.discovery.ServiceConfigurationResolver] found for dependency: 
expected at least 1 bean which qualifies as autowire candidate for this dependency. 
Dependency annotations: {}; nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException: 
No qualifying bean of type [com.ofg.infrastructure.discovery.ServiceConfigurationResolver] found for dependency: 
expected at least 1 bean which qualifies as autowire candidate for this dependency. Dependency annotations: {}
```

### Workaround:

You have legacy code that requires JSON representation whereas right now you have to use the ZookeeperDependencies class. 
Together with ZookeeperDiscoveryProperties to retrieve the realm.

## Application name is wrong!

There is a bug in Spring Boot - it should be fixed in 1.3.0.RC2 (https://github.com/spring-projects/spring-boot/pull/4362)

### Workaround

Pass `spring.application.name` via system prop or command line. For example:

```
./gradlew bootRun -Dspring.profiles.active=prod,springCloud -DAPP_ENV=prod -DCONFIG_FOLDER=/properties -Dserver.port=9090 -Dspring.application.name=foo
```

## Port in Zookeeper is wrong (the app booted correctly)

### Issue
 
There is a bug in Spring Boot - it should be fixed in 1.3.0.RC2 (https://github.com/spring-projects/spring-boot/pull/4362)

### Workaround

Pass `server.port` via system prop or command line.  For example:

```
./gradlew bootRun -Dspring.profiles.active=prod,springCloud -DAPP_ENV=prod -DCONFIG_FOLDER=/properties -Dserver.port=9090 -Dspring.application.name=foo
```

## I want to use the old functionality of micro-infra but it blows up with Spring Cloud stuff

Ensure that you have a `bootstrap.yaml` file on your classpath that deregisters everything related to Spring Cloud. For example:

```
spring.cloud.zookeeper.enabled: false
spring.cloud.zookeeper.discovery.enabled: false
ribbon.zookeeper.enabled: false
spring.autoconfigure.exclude: org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration
```
