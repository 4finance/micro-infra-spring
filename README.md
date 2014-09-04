[![Build Status](https://travis-ci.org/4finance/stub-runner.svg?branch=master)](https://travis-ci.org/4finance/stub-runner)[![Coverage Status](http://img.shields.io/coveralls/4finance/stub-runner/master.svg)](https://coveralls.io/r/4finance/stub-runner)

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

### Stub runner configuration

Under `ext` block inside `build.gradle` you can configure stub runner with the following properties:
* stubRepositoryPath
* stubRegistryPort
* minStubPortNumber
* maxStubPortNumber
* context
    
Project metadata that will be loaded is derived from the executed task name, e.g.: `gradle runStubsHealthCheck` will register collaborators defined in `healthCheck.json`.

### Defining collaborators' stubs

#### Defining service collaborators

Service collaborators are defined in project metadata file (JSON document) with the following structure:
```
{
    "context": [ Fully qualified names of your collaborators ]
}
```

Example:
```
{
    "pl": [
        "com/ofg/foo",
        "com/ofg/bar"
    ]
}
```

By default project metadata definitions are stored at stub repository root

#### Stubbing collaborators

For each collaborator defined in project metadata all collaborator mappings (stubs) available in repository are loaded and registered in service registry (Zookeeper).
Stubs are defined in JSON documents, whose syntax is defined in [WireMock documentation](http://wiremock.org/stubbing.html)

Example:
```
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