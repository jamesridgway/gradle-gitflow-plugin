# gradle-gitflow-plugin
[![Build Status](https://travis-ci.org/jamesridgway/gradle-gitflow-plugin.svg?branch=master)](https://travis-ci.org/jamesridgway/gradle-gitflow-plugin)
[ ![Download](https://api.bintray.com/packages/jamesridgway/gradle-plugins/uk.co.jamesridgway%3Agradle-gitflow-plugin/images/download.svg) ](https://bintray.com/jamesridgway/gradle-plugins/uk.co.jamesridgway%3Agradle-gitflow-plugin/_latestVersion)

This project is a GitFlow versioning plugin for gradle.

This plugin will infer the version number to use for your project based on the state of the git repository.

## Usage
Apply the plugin and add it as a classpath dependency as demonstrated below:

Example `build.gradle`:

```groovy
apply plugin: 'uk.co.jamesridgway.gradle.gitflow.plugin'

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'uk.co.jamesridgway:gradle-gitflow-plugin:1.5.0'
    }
}
```


That's all it takes, the plugin will set the `version` attribute of your build automatically.

## Configuration
The unreleased version format can be configured:

```groovy
gitflow {
    // Customise template for unreleased version (uses freemarker)
    unreleasedVersionTemplate = '${major}.${minor}.${patch}.${commitsSinceLastTag}-${branch?replace("/", "_")}+sha.${commitId?substring(0,7)}${dirty?then(".dirty","")}'
}
```

## How does it work?
The plugin uses [jgit](https://github.com/eclipse/jgit) to infer the version number from the state of the repository.

The version can be inferred without needing to add or alter any tasks in the gradle task graph.

### Release Version
A tagged commit of the format `major.minor.patch` (on any branch) will be determined as a release version.

### Unreleased Version
For any commit that is not tagged with a release version (`major.minor.patch`) an "unreleased" version will be inferred.

By default, an unreleased version is as follows:

    0.0.1.1-feature/test1+sha.8661cfd.dirty
    | | | | |                 |       |
    | | | | |                 |       └ "dirty" is ppended if the commit is dirty
    | | | | |                 └ Short commit id
    | | | | └ Branch name
    | | | └ Number of commits since last tag
    | | └ Patch version for last release version
    | └ Minor version for last release version
    └ Major version for last release version

If the repository has never been tagged for a release the version will default to `0.0.0`
