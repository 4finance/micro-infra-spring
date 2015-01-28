#!/bin/bash -x

function dumpCurrentMicroInfraSpringVersionToFile {

    ./gradlew -q currentVersion | grep "Project version" | sed s/Project\ version\:\ //g > ~/.microInfraSpringCurrentVersion.txt
    cat ~/.microInfraSpringCurrentVersion.txt
}

function cloneAndDoBuild {

    echo Checking out and building $1 with micro-infra-spring `cat ~/.microInfraSpringCurrentVersion.txt`
    git clone https://github.com/4finance/"$1".git
    cd "$1"
    echo "microInfraSpringVersion="`cat ~/.microInfraSpringCurrentVersion.txt` >> gradle.properties
    cat gradle.properties
    ./gradlew check
    cd ..
}


set -e
dumpCurrentMicroInfraSpringVersionToFile
cloneAndDoBuild boot-microservice
#cloneAndDoBuild boot-microservice-gui
