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