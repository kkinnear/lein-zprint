# lein-zprint

A Leiningen plugin that will use the zprint library to format your
Clojure source files.  Zprint reformats Clojure source files from
scratch, completely ignoring all existing line breaks and white
space inside function definitions. Lein-zprint will invoke zprint 
on one or more source files that you specify.

Use boot instead of Leiningen?  No problem, use: [boot-fmt][bootfmt]

[boot-fmt][bootfmt] is usable even if you don't use boot, and has
different command line arguments. You might find
it more to your liking even if you use Leiningen.

The zprint library itself is highly configurable, allowing you 
to tune the formatting.

[bootfmt]: https://github.com/pesterhazy/boot-fmt


## Usage

It is pretty straightforward to use lein zprint.

Place `[lein-zprint "0.1.13"]` into the `:plugins` vector of your project.clj:

```
:plugins [[lein-zprint "0.1.13"]]
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
:plugins [[lein-zprint "0.1.13"]]
:zprint {:old? false}
...
```

Note: You should invoke `lein zprint` from the top level of your leiningen
project (i.e., the directory that contains the project.clj file).  While
you can invoke `lein zprint` from your source directory, the current directory
for `lein zprint` will be the top level of your leiningen project
and it will not find the files in the current directory.  Which will confuse
us all.

## Configuration

See [zprint](https://github.com/kkinnear/zprint) for information on how
to configure the the actual formatting performed by the zprint library.  

#### Overview

There are two ways that you will probably want to configure `lein zprint`,
though there are several more described below:

  * A `:zprint` key in your project.clj
  * Comment lines starting with `;!zprint` in your source file

#### Details

You can use the following approaches when configuring lein zprint:

  * The `$HOME/.zprintrc` file, containing a zprint options map, as described in
  the zprint readme.  This configuration information will be used for all invocations
  of the zprint library by this user -- including lein zprint and repl calls.

  * Environment variables, as described in the zprint readme. 

  For instance, you can change the width from 80 (the default) to 90 by:

    ```
    export zprint__width=90
    ```

  This too will be used for all lein zprint and repl calls initiated from this
  particular shell.

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



## License

Copyright © 2016 Kim Kinnear

Distributed under the MIT License.  See the file LICENSE for details.
