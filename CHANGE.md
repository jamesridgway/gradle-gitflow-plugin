# Change Log

## 1.5.2
* Release build fix

## 1.5.1
* Fixed tag parsing issue

## 1.5.0
* Fix configuration of 'unreleaseVersionTemplate'

## 1.4.0
* Optimise most recent tag lookup

## 1.3.0
* Improved performance for identifying the most recent tag.

## 1.2.0
* Upgraded to Gradle 4.5
* Fixed "Duplicate key" issue with multiple release tags on a non-HEAD commit.
* A release-tagged commit was still being considered as a release version even with dirty changes
* Upgraded jgit from 4.9.0.201710071750-r to 4.10.0.201712302008-r.
## 1.1.0
* Added `isRelease()` to `Version` interface to allow for determining of release version
* By default substitute `/` for `_` in branch names to add maven compatibility out of the box.

## 1.0.1
* Initial release
