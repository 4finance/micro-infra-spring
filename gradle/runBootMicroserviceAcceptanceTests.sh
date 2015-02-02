#!/bin/bash -x

function dumpCurrentMicroInfraSpringVersionToFile {

    ./gradlew -q currentVersion | grep "Project version" | sed s/Project\ version\:\ //g > ~/.microInfraSpringCurrentVersion.txt
    cat ~/.microInfraSpringCurrentVersion.txt
}

function cloneAndDoBuild {

    echo Checking out and building $1 with micro-infra-spring `cat ~/.microInfraSpringCurrentVersion.txt`
    # TODO: Verify depth=1 with the second branch
    git clone --depth=1 https://github.com/4finance/"$1".git
    cd "$1"
    echo "microInfraSpringVersion="`cat ~/.microInfraSpringCurrentVersion.txt` >> gradle.properties
    cat gradle.properties
    ./gradlew check --stacktrace --continue
    cd ..
}


set -e
dumpCurrentMicroInfraSpringVersionToFile
cloneAndDoBuild boot-microservice
#cloneAndDoBuild boot-microservice-gui
