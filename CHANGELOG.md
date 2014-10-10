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