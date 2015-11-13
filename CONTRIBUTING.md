# Before submitting pull request

* Ensure that you wrote as much code as possible in Java 7
* Your branch should be named `issues/XYZ` (or `issues/XYZ-shortDesc`), where `XYZ` is an issue number on GitHub. When your pull request is not related to any issue, branch name is arbitrary
* It is important to put meaningful description (in addition to issue number) in a pull request name
* Run full build:

        $ ./gradlew clean build

* Make sure you wrote unit tests, both for new features or bug fixes
* Add JavaDoc when applicable
* You should avoid breaking backward compatibility, especially repackaging and changing `public` methods
* Be careful when changing dependencies
* You don't have to assign person and milestone
* Document new features in [Wiki](https://github.com/4finance/micro-infra-spring/wiki), especially new [configuration properties](https://github.com/4finance/micro-infra-spring/wiki/Configuration) after they are merged

# Rebasing your pull request
You should prefer rebasing your pull request instead of merging it. Let's assume that you have
a branch named `cool-feature`. Here are instructions needed to rebase your branch on top of master:

        $ git checkout cool-feature
        $ git pull --rebase origin master
        (Resolve conflicts if any. Squash commits if necessary.)
        $ git push origin cool-feature -f
        (Wait for Travis/Snap CI results, making sure your code isn't broken after rebase.)
        $ git checkout master
        $ git merge --ff-only cool-feature
        $ git push origin master

GitHub will detect automatically these operations and mark your pull request as merged.

**Note**. If you prefer to merge with --ff-only from GUI (when possible) go to GitHub [contact section](https://github.com/contact) and write about it. Sample message to copy/paste.

> Hi. It would be useful to be able to optionally merge PRs without a merge commit from GUI (if there were no other commits to master in the meantime).

# Maintaining the "legacy" version

In master ATM we have a version that supports Spring Cloud. There is a branch called "legacy" where we have the version that is not using Spring Cloud at all.
If you want your feature to be present for applications that are using the legacy version of micro-infra-spring you'll have to apply your change also to that branch.