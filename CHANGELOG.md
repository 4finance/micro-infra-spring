0.4.3
-----
New features:
* default JUnit test classes

0.4.2
------
New features:
* [micro-deps](https://github.com/4finance/micro-deps) upgraded to version `0.5.6`

0.4.1
------
Bug fixes:
* [WireMock.resetMappings() deletes mappings taken from files](https://github.com/4finance/micro-deps-spring-config/issues/3)

0.4.0
-----
New features:
* default profiles
* [micro-deps](https://github.com/4finance/micro-deps) upgraded to version `0.5.5`

Bug fixes:
* [Log4j stuff included in fatJar even though there are excludes present](https://github.com/4finance/micro-deps-spring-config/issues/1)

Breaking changes:
* in dev & test profiles `com.ofg.infrastructure.discovery.StubbedServiceResolver` is used in place of `com.ofg.infrastructure.discovery.ZookeeperServiceResolver`
* removed `com.ofg.infrastructure.discovery.ServiceDiscoveryStubbingConfiguration`

0.3.0
-----
New features:
* [micro-deps](https://github.com/4finance/micro-deps) upgraded to version `0.5.4`

Breaking changes:
* `service.resolver.connection.retries` property renamed to `service.resolver.connection.retry.times`
* `service.resolver.connection.timeout` property renamed to `service.resolver.connection.retry.wait`

0.2.2
-----
New features:
* Groovy sources compiled with Java-style compile time checks 

0.2.1
-----
New features:
* [micro-deps](https://github.com/4finance/micro-deps) upgraded to version `0.5.1`

0.2.0
-----
New features:
* [micro-deps](https://github.com/4finance/micro-deps) upgraded to version `0.5.0`

0.0.8
-----
New features:
* `micro-deps-spring-test-config` artifact 

0.0.7
-----
New features:
* `MicroserviceAddressProvider` scope changed to public

0.0.6
-----
New features:
* service instance default host and port provider

0.0.5
-----
New features:
* loading microservice configuration files not available on classpath

0.0.4
-----
Bug fixes:
* excluded logging libraries from micro-deps transitive dependencies

0.0.3
-----
New features:
* microservice context removed from instance uri spec

Breaking changes:
* `microservice.uri` property renamed to `microservice.host`

0.0.2
-----
New features:
* Java 7 compatibility
* default service resolver configuration
* [micro-deps](https://github.com/4finance/micro-deps) upgraded to version `0.3.1`

0.0.1
-----
Initial release