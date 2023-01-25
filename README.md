# lein-zprint

A Leiningen plugin that will use the zprint library to format your
Clojure source files.  Zprint reformats Clojure source files from
scratch, completely ignoring all existing line breaks and white
space inside function definitions. Lein-zprint will invoke zprint 
on one or more source files that you specify.  

Lein-zprint will also check to see if files are correctly formatted, without
changing them.

See the considerable [documentation for zprint](https://github.com/kkinnear/zprint).

If you would prefer a standalone program that accepts Clojure(script)
source and emits formatted Clojure(script) source, starts up faster
than 55ms, and runs faster than anything using Javascript (e.g. node.js),
look here 
[here](https://github.com/kkinnear/zprint/blob/master/doc/using/files.md)
for information!

Note that the pre-built binaries discussed in the link above will (as of
version `zprint 1.2.6`) perform essentially the same operations as lein zprint.
You can do:
```
$ zprint -w *.clj
```
to format a series of Clojure(script) files.  This approach will almost 
certainly run faster than lein zprint, possibly taking 60% of the time as
lein zprint.  The only substantive difference is that it won't read 
zprint configuration from the `project.clj` file.

The zprint library itself is highly configurable, allowing you to
tune the formatting.  

## To configure lein-zprint in its normal, highly configurable way:

* Use `$HOME/.zprintrc` or `$HOME/.zprint.edn`
* Define `:search-config?` as `true` in your `$HOME/.zprintrc` or 
`$HOME/.zprint.edn` file, and then configure a `.zprintrc` or `.zprint.edn`
file in the current directory or any parent of that directory.  Zprint will
use the first file it finds when searching up from the current directory.
* Place a `:zprint {}` options map in your `project.clj` file



[bootfmt]: https://github.com/pesterhazy/boot-fmt


## Usage

### Formatting Files

It is pretty straightforward to use lein zprint.

Place `[lein-zprint "1.2.6"]` into the `:plugins` vector of your project.clj:

```
:plugins [[lein-zprint "1.2.6"]]
```

Then, to format a source file, simply invoke `lein zprint` on that file: 


    $ lein zprint src/<project>/<file-name>.clj


Lein-zprint will __replace__ the existing file with one formatted by zprint.
It will also __rename__ __the__ __existing__ __file__ by appending a `.old` extension 
to the file (after __removing__ any existing file with that name and an
`.old` extension).

If you don't like the results, you can easily replace the new file by
removing it and renaming the `.old` by removing the `.old` extension.
Of course, you can only do this once, as `lein zprint` will continue
to replace the `.old` file with the latest file whenever it is run.

Renaming the initial file with a .old extension can be disabled by 
setting a zprint options map in your project.clj:

```
...
:plugins [[lein-zprint "1.2.6"]]
:zprint {:old? false}
...
```

NOTE: You should invoke `lein zprint` from the top level of your leiningen
project (i.e., the directory that contains the project.clj file).  While
you can invoke `lein zprint` from your source directory, the current directory
for `lein zprint` will be the top level of your leiningen project
and it will not find the files in the current directory.  Which will confuse
us all.


### Checking Files

You can use `lein-zprint` to check to see if files are formatted correctly,
without changing them.  Use the `-c` or `--check` command line switches
to access this capability.  When checking files, in addition to printed
messages, the exit status is 0 (for success) if all of the files were 
formatted correctly.  The exit status is 1 (for failure) if any of the files
were not formatted correctly.

```
√ % lein zprint -c correctcheck
Processing file: correctcheck
All files formatted correctly.
√ %
```

Correctly formatted is defined as whatever formatting `lein-zprint` would 
perform based on the configuration it uses when invoked for checking the files.

```
√ % lein zprint -c proj.clj correctcheck
Processing file: proj.clj
File: proj.clj was incorrectly formatted
Processing file: correctcheck
1 file formatted incorrectly
?1 %
```

## Configuring lein zprint to operate on source files

See [zprint](https://github.com/kkinnear/zprint) for information on how
to configure the the actual formatting performed by the zprint library.  

#### Overview

There are two ways that you will probably want to configure `lein zprint`,
though there are several more described below:

  * A `:zprint` key in your project.clj
  * Comment lines starting with `;!zprint` in your source file

#### Details

You can use the following approaches when configuring lein zprint:

  * The `$HOME/.zprintrc` file, containing a zprint options map,
  as described in the zprint readme.  This configuration information
  will be used for all invocations of the zprint library by this
  user -- including lein zprint and zprint calls made when running
  in the REPL.

  * A `:zprint` options map in `project.clj`.

  Again, you could change the width for the project this way 
  and turn off the renaming of files with a .old extension
  (as well as configure any of the myriad aspects of the zprint library):

    ```
    ...
    :zprint {:width 90 :old? false}
    ...
    ```
  This only affects the configuration for lein zprint, it is not recognized by
  repl (c)zprint or (c)zprint-fn calls.  That said, it is likely the place where
  you will want to configure a width and turn off the renaming of files with
  a .old extension, as above.

  * A `.zprintrc` file in the top level of the project. 

  You can put a .zprintrc file in your project (at the same level as the
  `project.clj` file), and it will be recognized and used for configuring
  lein zprint if you __also__ put `{:search-config? true}` in the `:zprint`
  options map in the `project.clj` file.  

  * Arguments on the command line of lein zprint.

  The first argument to lein zprint can be a number or an options
  map.  If it is a number, it is used as the width.  If it is an
  options map, it is used to configure zprint.  If the first argument
  is a number, the second argument can be an options map.  Note
  that an options map must be surrounded by single or double quotes.

  Here is yet another way to change the width to 90:

  ```
  lein zprint 90 src/myproject/*.clj
  ```

  and one other (of course this options map can contain anything
  allowed to configure zprint -- see the zprint readme).

  ```
  lein zprint '{:width 90}' src/myproject/*.clj
  ```

There are so many ways to configure zprint (and even more for lein
zprint), that you might end up wondering what configuration you are
actually using.  To find out, you can place the special token
`:explain` anywhere in the arguments list, and lein zprint will
output the current options map with information explaining where
all of the non-default values came from.  Thus,

```
lein zprint 95 :explain src/myproject/*.clj
```

will set the width to 95, output the options map to standard out,
and then format all of the .clj files in src/myproject.

#### Configuring lein zprint in your source file

No matter how you set up the global zprint configuration for your
project, there will be parts of some of your source files that you
want configured differently.  You may simply want to disable lein zprint
from part of a file, or you may want to configure the zprint formatting
engine to operate differently for a single function definition (or
for a segment of the file).  All of these things are possible.

Lein zprint will examine every "top level" comment (that is, every comment
not inside a collection in the file) for the following signature:

```
;!zprint 
```

When it finds a comment line that begins with ";!zprint ", it will read the
rest of the line as a Clojure s-expression, and assume that it is a
zprint options map.  In addition, there is one additional key for the
options map that zprint ignores but which is very important to lein zprint,
the `:format` key.  Using the `:format` key, you can:

  * Turn off formatting in the file:

  ```
  ;!zprint {:format :off}
  ```

  * Turn on formatting in the file (it is on by default):

  ```
  ;!zprint {:format :on}
  ```

  * Skip formatting the next non-whitespace, non-comment element in the
  files (e.g., the next function definition):

  ```
  ;!zprint {:format :skip}
  ```

  * Change the formatting used for the remainder of the file (unless it
  is changed again, of course):

  ```
  ;!zprint {<zprint-options>}
  ```

  * Change the formatting used for the next non-whitespace and non-comment
  element of the file (e.g., the next function definition):

  ```
  ;!zprint {:format :next <zprint-options>}
  ```

As much as it would be nice to have one zprint formatting configuration be
perfect for every function and part of your source file, I haven't found
that to be the case for my sources, and you probably won't either.  For
instance, the help text you get from `lein zprint :help` (see below) is
organized as a vector of strings, one per line.  If I let lein zprint loose
on it, it wraps all of the strings as tightly as it can into 80 columns,
and I can't see what it looks like.  So I do this before the definition
of the help text:

```
;!zprint {:format :next :vector {:wrap? false}}
```

Which makes that one def look a lot better.

##### Not getting what you expect?

If a `;!zprint ` directive is itself formatted incorrectly, you will get
output indicating the problem.  

You can place an `:explain` element after a file with `;!zprint ` formatting
directives in it, and you will see the values of all of the zprint formatting
options set when the file exited and before they are cleared when the next
file is begun.  This is scant help for what is happening inside of the
file, but it can be helpful nonetheless.  Any changes made by `;!zprint `
directives are shown, and the directive is identified by its number in
the file.

#### Help!

Finally, if you forget any of this, you can always type:

```
lein zprint :help
```

which will output some helpful reminders, including all of the {:format ...}
information above..

## Support

If you have problems with lein zprint, please create an issue.  Please include
the output from:

```
lein zprint :support <whatever-you-put-here-that-did-not-work-right> :support
```

as well as the source file (or fragment thereof) that is a concern,
and please show what lein zprint produced and explain how it differed
from what you expected. 

## Limiting Configuration for lein-zprint

Note that some people want a formatter which is less configurable.
To that end, lein-zprint will also support specification of its operation
with only very small number of fixed configurations.  At present, the
only fixed configuration is __default__, where lein-zprint will accept
no external configuration, and will format based only on the default
configuration and the information in the file.  

If you specify default configuration lein-zprint will ignore all
external configuration, including the `$HOME/.zprintrc` file and
most of the information in the `:zprint` key in the `project.clj` file.
The only external configuation it will accept (which doesn't affect
the formatting) are the `:old?` and `:parallel?` keys in the `:zprint` 
map in the `project.clj` file.

This default configuration is available in two ways:

  * If you specify "-d" or "--default" on the command line in place of
    an options map. 

  * If you specify `:command :default` in the `:zprint` map in the
    `project.clj` file.  Note that you cannot specify this in the
    `$HOME/.zprintrc` file, it is only available to the `:zprint`
    map in the `project.clj` file!  By using this approach, a project
    can enforce formatting that is not affected by a users default
    zprint configuration or by any options map given on the lein
    zprint command line.



## License

Copyright © 2016-2023 Kim Kinnear

Distributed under the MIT License.  See the file LICENSE for details.
