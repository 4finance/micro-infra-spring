Stub-runner
===========

Runs stubs for service collaborators. Treating stubs as contracts of services allows to use stub-runner as an implementation of [Consumer Driven Contracts](http://martinfowler.com/articles/consumerDrivenContracts.html).

### Running stubs

#### Running using main app

You can set the following options to the main class:

```
java -jar stub-runner.jar [options...] 
 -c (--context) VAL                 : Context for which the project should be
                                      run (e.g. 'pl', 'lt')
 -maxp (--maxPort) N                : Maximum port value to be assigned to the
                                      Wiremock instance. Defaults to 15000
 -md (--useMicroserviceDefinitions) : Switch to define whether you want to use
                                      the new approach with microservice
                                      definitions. Defaults to 'true'. To use
                                      old version switch to 'false'
 -minp (--minPort) N                : Minimal port value to be assigned to the
                                      Wiremock instance. Defaults to 10000
 -n (--serviceName) VAL             : Name of the service under which it is
                                      registered in Zookeeper. (e.g.
                                      'com/service/name')
 -r (--repository) VAL              : @Deprecated - Path to repository
                                      containing the 'repository' folder with
                                      'project' and 'mapping' subfolders (e.g.
                                      '/home/4finance/stubs/')
 -s (--skipLocalRepo)               : @Deprecated - Switch to check whether local
                                      repository check should be skipped and dependencies
                                      should be grabbed directly from the net.
                                      Defaults to 'true'
 -sg (--stubsGroup) VAL             : @Deprecated - Name of the group where you
                                      store your stub definitions (e.g. com.ofg)
 -sm (--stubsModule) VAL            : @Deprecated - Name of the module where
                                      you store your stub definitions (e.g.
                                      stub-definitions)
 -sr (--stubRepositoryRoot) VAL     : Location of a Jar containing server where
                                      you keep your stubs (e.g. http://nexus.4fi
                                      nance.net/content/repositories/Pipeline)
 -ss (--stubsSuffix) VAL            : Suffix for the jar containing stubs (e.g.
                                      'stubs' if the stub jar would have a
                                      'stubs' classifier for stubs:
                                      foobar-stubs ). Defaults to 'stubs'
 -wo (--workOffline)                : Switch to work offline. Defaults to
                                      'false'
 -wsc (--waitForServiceConnect)     : Switch to wait for service registration
                                      in Zookeeper (default timeout is 60
                                      seconds - configurable using -wt)
 -wt (--waitTimeout) N              : Amount of second to wait for service
                                      registration
 -z (--zookeeperPort) N             : Port of the zookeeper instance (e.g. 2181)
 -zl (--zookeeperLocation) VAL      : Location of local Zookeeper you want to
                                      connect to (e.g. localhost:23456)
```

##### Running on an environment with already existing Zookeeper

When you want to register your stubs against an already existing Zookeeper instance it's enough to provide a switch

```
-zl localhost:1234
```

that will point to the place where the Zookeper instance can be found

##### Running on an environment without a Zookeeper

When you want to register your stubs against embedded Zookeeper instance it's enough to provide a switch

```
-z 1234
```

that will start a testing Zookeeper instance on port 1234


##### Configuring wait time for registering services

There is a option to wait given amount of time waiting for service to be registered using an application parameter called:

```
-wsc 
```

after switching it on stub-runner will wait default 30 second for service registration

### Stub runner configuration

You can configure the stub runner by either passing the full arguments list with the `-Pargs` like this:

```
./gradlew stub-runner-root:stub-runner:run -Pargs="-c pl -minp 10000 -maxp 10005 -n com/ofg/twitter-places-analyzer -sr http://dl.bintray.com/4finance/micro -zl localhost:2181 -s"
```

or each parameter separately with a `-P` prefix and without the hyphen `-` in the name of the param

```
./gradlew stub-runner-root:stub-runner:run -Pc=pl -Pminp=10000 -Pmaxp=10005 -Pn=com/ofg/twitter-places-analyzer -Psr=http://dl.bintray.com/4finance/micro -Pzl=localhost:2181 -Ps
```

### Defining collaborators' stubs

#### Global vs Realm specific stubs

You can define global stubbing behaviour (Wiremock will be fed with those stubs for all contexts) under the folder: 

```
com/ofg/foo
```

To override existing or add new behaviour that is specific to this realm just place your mappings under:

```
pl/com/ofg/foo
```

We ensure such ordering:

* Global mappings will be registered in Wiremock
* Afterwards realm specific stubs will get registered

By default stub definitions are stored in `mappings` directory inside stub repository.

#### Stubbing collaborators

For each collaborator defined in project metadata all collaborator mappings (stubs) available in repository are loaded and registered in service registry (Zookeeper).
Stubs are defined in JSON documents, whose syntax is defined in [WireMock documentation](http://wiremock.org/stubbing.html)

Example:
```json
{
    "request": {
        "method": "GET",
        "url": "/ping"
    },
    "response": {
        "status": 200,
        "body": "pong",
        "headers": {
            "Content-Type": "text/plain"
        }
    }
}
```

Stub definitions are stored in stub repository under the same path as collaborator fully qualified name.
Paths (as long it's inside the directory mentioned above) and names of documents containing stub definitions not play any other role than describing stubs' role / purpose.

#### Viewing registered mappings

Every stubbed collaborator exposes list of defined mappings under `__/admin/` endpoint.

## Other projects basing on *stub-runner*

- [Stub Runner Spring](https://github.com/4finance/micro-infra-spring/wiki/Stub-runner) - adds automatic downloading of the newest versions of stubs to your tests
