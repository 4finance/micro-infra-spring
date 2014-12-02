0.2.0
-----
Notable changes:
* [stub-runner](https://github.com/4finance//stub-runner) upgraded to version `0.3.1
* New configuration parameter added, i.e. `stubrunner.use.local.repo`. See project's wiki for more details.

New features:
* [Issue 2](https://github.com/4finance/stub-runner-spring/issues/2) Downloading increases build time by ~ 10 sec
* [Issue 4](https://github.com/4finance/stub-runner-spring/issues/4) It's rather impossible to work offline with current setup
* [Issue 5](https://github.com/4finance/stub-runner-spring/issues/5) Make deletion of downloaded grapes optional (or even remove it)

0.1.0
-----
Notable changes:
* [stub-runner](https://github.com/4finance//stub-runner) upgraded to version `0.2.2`
* [micro-deps](https://github.com/4finance/micro-deps) upgraded to version `0.7.0`

New features:
* [Issue 6](https://github.com/4finance/stub-runner-spring/issues/6) Add support for header properties in JSON config for a dependency

Breaking changes:
* `StubRunnerConfiguration` moved to `com.ofg.stub.config` package
* `StubRunnerConfiguration.BatchStubRunner` inner class moved to separate `BatchStubRunner` class
* Required format of dependencies specified in JSON file has been change - check README file in [micro-deps](https://github.com/4finance/micro-deps) project for details.

0.0.1
-----
Initial release
