0.3.2
-----
Bug fixes:
* [Issue 16](https://github.com/4finance/stub-runner/issues/16) logback.xml should not be packaged into JAR

New features:
* [Issue 15](https://github.com/4finance/stub-runner/issues/15) Remove project definition from json file
* [Issue 20](https://github.com/4finance/stub-runner/issues/20) Switch production releasing to new mechanism

0.3.1
-----
Bug fixes:
* [Issue 17](https://github.com/4finance/stub-runner/issues/17) Remove dependency on Servlet API v2.5

New features:
* [Issue 14](https://github.com/4finance/stub-runner/issues/14) Switch releasing to Axion plugin

0.3.0
-----
New features:
* [Issue 10](https://github.com/4finance/stub-runner/issues/10) Testing zookeeper should be enabled/disabled on a switch

Bug fixes:
* [Issue 12](https://github.com/4finance/stub-runner/issues/12) Incorrect mappings are registered when running gradle runStubs

0.2.2
-----
Notable changes:
* `StubRunning` interface extends `Closeable` interface

0.2.1
-----
Bug fixes:
* Fixed a bug related to picking up repositoryPath from field instead of arguments

0.2.0
-----
New features:
* Added Spring Boot plugin for fat jar creation
* Added Args4j for options setting

0.1.0
-----
New features:
* [Issue 6](https://github.com/4finance/stub-runner/issues/6) Merge global context and realm context mappings

Breaking changes:
* [Issue 8](https://github.com/4finance/stub-runner/issues/8) Project definitions should not be kept in the same directory as stub definitions

0.0.1
-----
Initial release
