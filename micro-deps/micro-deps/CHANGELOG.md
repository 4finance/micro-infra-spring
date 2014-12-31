0.7.3
-----
New features:
* [Issue 29](https://github.com/4finance/micro-deps/issues/29) Storing microservice dependencies in Zookeeper

0.7.2
-----
Notable changes:
* Change default retry policy in `MicroDepsService` from retry N times to exponential backoff

Bug fixes:
* [Issue 28](https://github.com/4finance/micro-deps/issues/28) Bring back backward compatibility with microservice.json

0.7.1
-----
Bug fixes:
* [Issue 25](https://github.com/4finance/micro-deps/issues/25) Better exception message on missing element in JSON

0.7.0
-----
New features:
* [Issue 15](https://github.com/4finance/micro-deps/issues/15) Add version property and headers properties in JSON config for a dependency

Breaking changes:
* Required format of dependencies specified in JSON file has been change - instead of a map with name-path pairs there is a map with service name as the key and nested map with configuration properties as the value. For more details check 'Dependencies' section in README file.

0.6.2
-----
Bug fixes:
- fixed service cache changes log message

Breaking changes:
* `ServiceResolver.fetchAllServiceNames` method renamed to `fetchCollaboratorsNames`

0.6.1
-----
New features:
* new controller providing current status of connection with services the microservice depends upon

0.6.0
-----
New features:
* allow to skip dependencies section in microservice configuration if microservice has no dependencies

Breaking changes:
* when service is unavailable `ServiceUnavailableException` is thrown instead of `ServiceNotFoundException`

0.5.6
------
Breaking changes:
* `fetchUrl` method added to `ServiceResolver` interface