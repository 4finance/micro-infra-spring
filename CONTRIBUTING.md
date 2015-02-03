# Before submitting pull request

* Your branch should be named `issues/XYZ` (or `issues/XYZ-shortDesc`), where `XYZ` is an issue number on GitHub. When your pull request is not related to any issue, branch name is arbitrary
* It is important to put meaningful description (in addition to issue number) in a pull request name
* Run full build:

        ./gradlew clean build

* Make sure you wrote unit tests, both for new features or bug fixes
* Add JavaDoc when applicable
* You should avoid breaking backward compatibility, especially repackaging and changing `public` methods
* Be carefull when changing dependencies
* You don't have to assign person and milestone
* Document new features in [Wiki](https://github.com/4finance/micro-infra-spring/wiki), especially new [configuration properties](https://github.com/4finance/micro-infra-spring/wiki/Configuration) after they are merged
