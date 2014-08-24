[![Build Status](https://travis-ci.org/4finance/stub-runner.svg?branch=master)](https://travis-ci.org/4finance/stub-runner)

Stub-runner
===========

Runs stubs for service collaborators

### Running stubs

#### Running specified stubs
To run stubs execute `gradle runStubs<ProjectMetadataFileWithoutExtension>`.

For example: to run stubs for `healthCheck` project execute `gradle runStubsHealthCheck`.
This will:
* start Zookeeper
* run stubs for projects defined in healthCheck.json
* register stubs in Zookeeper

#### Running all stubs
To run all stubs execute `gradle runStubs`.

This will:
* start Zookeeper
* run stubs for projects defined in folders that don't have any children inside - for example:
```
Having such a folder structure
- /com/ofg/foo/foo.json
- /com/ofg/foo/bar/bar.json
```

it will register stubs in the __/com/ofg/foo/bar/__ folder while ignoring the __foo.json__ file

* register stubs in Zookeeper