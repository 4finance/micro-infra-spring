Microservice Dependency Manager Spring Configuration
====================================================

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

# initial amount of milliseconds to wait between retries
service.resolver.connection.retry.baseSleepMs=50

# max number of retries
service.resolver.connection.retry.maxRetries=20

# max amount of milliseconds to wait between retries
service.resolver.connection.retry.maxSleepMs=500

```

Take a look at [stub-runner-spring project](https://github.com/4finance/micro-infra-spring/wiki/Stub-runner) for more information on our [Consumer Driven Contracts](http://martinfowler.com/articles/consumerDrivenContracts.html) implementation. Below you can find the properties that you can set in that regard together with default values:

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

### Zookeeper connectors

Running microservice with `com.ofg.config.BasicProfiles.PRODUCTION` profile active will try to connect to a standalone Zookeeper instance.
Otherwise, an in-memory instance of Zookeeper will be started. For development purposes, you can enable standalone Zookeeper connector manually
via environment property `zookeeper.standalone.enabled`.