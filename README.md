[![Build Status](https://travis-ci.org/4finance/stub-runner.svg?branch=master)](https://travis-ci.org/4finance/stub-runner)

Stub-runner
===========

Runs stubs for service collaborators

### Running stubs

To run stubs execute `gradle runStubs<ProjectMetadataFileWithoutExtension>`.

For example: to run stubs for `healthCheck` project execute `gradle runStubsHealthCheck`.
This will:
* start Zookeeper
* run stubs for projects defined in healthCheck.json
* register stubs in Zookeeper
