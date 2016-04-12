#!/bin/bash -x

function dumpCurrentMicroInfraSpringVersionToFile {

    ./gradlew -q currentVersion | grep "Project version" | sed s/Project\ version\:\ //g > ~/.microInfraSpringCurrentVersion.txt
    cat ~/.microInfraSpringCurrentVersion.txt
}

function updateMicroInfraSpringVersionInConfigurationFile {
    echo -e "\nmicroInfraSpringVersion="`cat ~/.microInfraSpringCurrentVersion.txt` >> gradle.properties
    cat gradle.properties
}

set -e
dumpCurrentMicroInfraSpringVersionToFile

git clone https://github.com/4finance/boot-microservice.git
cd boot-microservice
updateMicroInfraSpringVersionInConfigurationFile
./gradlew clean check guiTest uptodate --stacktrace --continue --no-daemon
