# Before submitting pull request

* Your branch should be named `issues/XYZ`, where `XYZ` is an issue number on GitHub. When your pull request is not related to any issue, branch name is arbitrary
* Run full build:

        ./gradlew clean build

* Make sure you wrote unit tests, both for new features or bug fixes
* Add JavaDoc when applicable
* You should avoid breaking backward compatibility, especially repackaging and changing `public` methods
* Be carefull when changing dependencies
* You don't have to assign person and milestone