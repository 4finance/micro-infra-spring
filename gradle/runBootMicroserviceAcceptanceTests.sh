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
git checkout tech/integration_with_sleuth
updateMicroInfraSpringVersionInConfigurationFile
./gradlew clean check uptodate --stacktrace --continue --info --no-daemon

git reset --hard
git checkout tech/integration_with_sleuth_gui
updateMicroInfraSpringVersionInConfigurationFile
./gradlew clean check --stacktrace --continue --info --no-daemon
