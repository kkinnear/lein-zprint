# Change Log
All notable changes to this project will be documented in this file. 

## 1.2.2 - 2022-01-26

### Added

### Changed

* Updated to [zprint "1.2.2"]

### Fixed

## 1.2.1 - 2022-01-26

### Added

### Changed

* Updated to [zprint "1.2.1"]

### Fixed

## 1.2.0 - 2021-11-18

### Added

### Changed

* Updated to [zprint "1.2.0"]

### Fixed


## 1.1.2 - 2021-5-1

### Added

### Changed

* Updated to [zprint "1.1.2"]

## 1.1.1 - 2021-1-18

### Added

### Changed

* Updated to [zprint "1.1.1"]

## 1.1.0 - 2021-1-16

### Added

### Changed

* Updated to [zprint "1.1.0"]

## 1.0.2 - 2020-11-10

### Added

### Changed

* Updated to [zprint "1.0.2"]

## 1.0.1 - 2020-9-21

### Added

### Changed

* Updated to [zprint "1.0.1"]

* Changed the way that files are processed, removing reliance on
  rename, and using just `slurp` and `spit` to read and create files.
  No temporary file created, it is all held in memory.  Which it was
  anyway in `zprint-file`, but now it is explicit here in `lein-zprint`.

## 1.0.0 - 2020-6-8

### Added

### Changed

* Updated to [zprint "1.0.0"]

## 0.5.4 - 2020-3-21

### Added

* You can now place a function in an options map specified on the command
line.  You cannot place a function in an options map specified in a
`project.clj` file.  Functions are rarely specified in an options map, so
this hasn't been much of a restriction, nor is the inability to specify it
in a `project.clj` file a serious problem.  You can always place a function
in an options map in the `~/.zprintrc` file.

* If you place `cwd-zprintrc?` or `search-config?` in the options map
specified on the command line or in the options map in the `project.clj` file,
they will be honored and used to when configuring the zprint library.

### Changed

* Updated to [zprint "0.5.4"]

## 0.5.3 - 2019-11-9

### Changed

* Updated to [zprint "0.5.3"]

## 0.5.2 - 2019-11-6

### Changed

* Updated to [zprint "0.5.2"]

## 0.5.1 - 2019-10-29

### Changed

* Updated to [zprint "0.5.1"]

* Removed planck and lumo zprint filter command line creation.

### Added

## 0.5.0 - 2019-10-19

### Changed

* Updated to [zprint "0.5.0"]

* Changed version number to match the version of zprint used ("0.5.0"), 
  instead of lagging behind by one version. So there was no version 
  `0.4.x` for lein zprint.  

### Added

* Limited configuration -- command line options `-d` or `--default`, or
  `:zprint` map in the `project.clj` file containing `:command :default`.

  If you specify default configuration lein-zprint will ignore all
  external configuration, including the `$HOME/.zprintrc` file and
  most of the information in the `:zprint` key in the `project.clj` file.
  The only external configuation it will accept (which doesn't affect
  the formatting) is the `:old?` key in the `:zprint` map in the
  `project.clj` file.

## 0.3.16 - 2019-6-12

### Changed

* Updated to [zprint "0.4.16"]

## 0.3.15 - 2019-1-14

### Changed

* Updated to [zprint "0.4.15"]


## 0.3.14 - 2019-1-13

### Changed

* Updated to [zprint "0.4.14"]


## 0.3.13 - 2018-11-21

### Changed

* Updated to [zprint "0.4.13"]

## 0.3.12 - 2018-11-6

### Changed

* Updated to [zprint "0.4.12"]

* Added test for ;!zprint support which allows any number of ;

## 0.3.11 - 2018-10-28

### Changed

* Updated to [zprint "0.4.11"]

## 0.3.10 - 2018-7-28

### Changed

* Updated to [zprint "0.4.10"]

## 0.3.9 - 2018-5-15

### Changed

* Updated to [zprint "0.4.9"]

## 0.3.8 - 2018-2-19

### Changed

* Updated to [zprint "0.4.7"]

### Fixed

* Force setting of `:parallel?` for each function.  Saves
  maybe 20% of time on longer files with big functions.

## 0.3.7 - 2018-1-10

### Changed

* Updated to [zprint "0.4.6"]

## 0.3.6 - 2017-12-9

### Changed

* Updated to [zprint "0.4.5"]

## 0.3.5 - 2017-11-10

### Fixed

* Fix problem with leiningen 2.8.1 and :lumo-cmd-line and
  :planck-cmd-line. Issue #2

## 0.3.4 - 2017-10-26

### Changed

* Updated to [zprint "0.4.4"]

## 0.3.3 - 2017-10-10

### Changed

* Updated to [zprint "0.4.3"]

## 0.3.2 - 2017-6-22

### Changed

* Updated to [zprint "0.4.2"]

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

