0.0.2-SNAPSHOT
-----
Notable changes:
* [stub-runner](https://github.com/4finance//stub-runner) upgraded to version `0.2.2`
* [micro-deps](https://github.com/4finance/micro-deps) upgraded to version `0.6.3`

New features:
* [Issue 6](https://github.com/4finance/stub-runner-spring/issues/6) Add support for header properties in JSON config for a dependency

Breaking changes:
* `StubRunnerConfiguration` moved to `com.ofg.stub.config` package
* `StubRunnerConfiguration.BatchStubRunner` inner class moved to separate `BatchStubRunner` class
* Required format of dependencies specified in JSON file has been change - check README file in [micro-deps](https://github.com/4finance/micro-deps) project for details.

0.0.1
-----
Initial release
