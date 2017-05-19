# Change Log
All notable changes to this project will be documented in this file. 

## 0.3.1 - 2017-5-18

### Changed

* Updated to [zprint "0.4.1"]

## 0.3.0 - 2017-5-5

### Changed

* Updated to [zprint "0.4.0"]

## 0.2.3 - 2017-5-4

### Changed

* Updated to [zprint "0.3.3"]

## 0.2.2 - 2017-4-18

### Changed

* Updated to [zprint "0.3.2"], which has bug-fix for Issue #23.

## 0.2.1 - 2017-4-10

### Changed

* Updated to [zprint "0.3.1"]

## 0.2.0 - 2017-4-9

### Changed

* Updated to [zprint "0.3.0"]

* Note that while configuration from environment variables and Java system
  properties still works in lein-zprint, that these capabilities are 
  __DEPRECATED__ as of zprint "0.3.0", and will likely go away at some
  point in the future.  If you care about them, file an issue!

## 0.1.16 - 2017-2-27

### Changed

* Added :lumo-cmd-line capability as well.  Similar to :planck-cmd-line
  but slightly different.

* Added :planck-cmd-line capability to output a single line script to
  make a pretty printing filter using planck.  Some minor tests and
  lots of documentation.

* Updated to [zprint "0.2.16"]

## 0.1.15 - 2017-1-24
### Changed

* Updated to [zprint "0.2.15"]

## 0.1.14 - 2017-1-22
### Changed

* Updated to [zprint "0.2.14"]

## 0.1.13 - 2017-1-19
### Changed

* Updated to [zprint "0.2.13"]

## 0.1.12 - 2017-1-9
### Changed

* Updated to [zprint "0.2.12"]

## 0.1.11 - 2017-1-8
### Changed

* Updated to [zprint "0.2.11"]

## 0.1.10 - 2016-12-23
### Changed

* Updated to [zprint "0.2.10"]

## 0.1.9 - 2016-11-13
### Changed

* Updated to [zprint "0.2.9"]

## 0.1.8 - 2016-11-9
### Changed

* Added announcement of rename of existing files with .old extensions
  with suggestion about how to stop this behavior.
* Added several tests involving :left-space :keep or :drop
* Updated to [zprint "0.2.8"]

## 0.1.7 - 2016-10-31
### Changed

* Updated to [zprint "0.2.7"]
* Added test for comment handling

* Fixed broken command line options

## 0.1.6 - 2016-10-06
### Changed

* Added small fixes to :help text
* Reformatted with updated zprint library

### Fixed

* Fixed broken command line options

## 0.1.5 - 2016-10-05
### Changed

* Added basic tests for command line operations
* Added basic tests for ;!zprint directive
* Added :support keyword for (zprint nil :support)
* Fixed initialization so that command line values show in :explain

### Fixed

* Fixed broken command line options

## 0.1.4 - 2016-09-29

### Fixed

* Fixed broken command line options

## 0.1.3 - 2016-09-29
### Changed

* Added :old? capability optional
* Added {:format ...} capability
* Documented that you need to be in the project.clj directory
* Linked to zprint 0.2.2

## 0.1.2 - 2016-09-22
### Changed

* Added alpha header to readme.
* Linked to zprint 0.2.1

## 0.1.1 - 2016-09-21
### Added
- Initial project commit.

