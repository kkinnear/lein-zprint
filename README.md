# lein-zprint

A Leiningen plugin that will use the zprint library to format your
Clojure source files.  Zprint reformats Clojure source files from
scratch, completely ignoring all existing line breaks and white
space inside function definitions. Lein-zprint will invoke zprint 
on one or more source files that you specify.  

It will also help you configure a Unix-style code formating filter
using the zprint library which you can use directly in your editor.

Use boot instead of Leiningen?  No problem, use: [boot-fmt][bootfmt]

[boot-fmt][bootfmt] is usable even if you don't use boot, and has
different command line arguments. You might find
it more to your liking even if you use Leiningen.

The zprint library itself is highly configurable, allowing you to
tune the formatting.  Note that as of zprint 0.3.0 (and lein-zprint
0.2.0) configuring zprint (and by extension lein-zprint) from system
environment variables and Java system properties is __DEPRECATED__,
though both will still work for the present with lein-zprint.

Use `$HOME/.zprintrc` or a `:zprint {}` options map in your
`project.clj` file to configure lein-zprint. 

[bootfmt]: https://github.com/pesterhazy/boot-fmt


## Usage

It is pretty straightforward to use lein zprint.

Place `[lein-zprint "0.3.5"]` into the `:plugins` vector of your project.clj:

```
:plugins [[lein-zprint "0.3.5"]]
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
:plugins [[lein-zprint "0.3.5"]]
:zprint {:old? false}
...
```

Note: You should invoke `lein zprint` from the top level of your leiningen
project (i.e., the directory that contains the project.clj file).  While
you can invoke `lein zprint` from your source directory, the current directory
for `lein zprint` will be the top level of your leiningen project
and it will not find the files in the current directory.  Which will confuse
us all.

# A zprint formatting filter using planck or lumo

_Wouldn't you really prefer a __Clojure uberjar__ which does the
same thing, starts up as fast, and runs faster for moderate to
large functions?  See [here](https://github.com/kkinnear/zprint/blob/master/doc/filter.md)
for how to get it!_

With a very small amount of setup, you can create a script which
will act as a "filter" and format Clojure or ClojureScript source
files.  Much like the Unix utility `fmt` will word wrap a paragraph
of text, you can create a similar utility that will pretty print a
bit of Clojure source.  Most editors have the ability to pipe a
section of text through a filter (like `fmt`) and replace the
original text by the output of the filter.  If you set up a zprint
filter, you can then pretty print a function in your editor with a
few keystrokes.

The only downside is that the startup and execution isn't instanteous
-- it takes a couple of seconds on my mid-2012 MacBook Air.  I'm
hoping to find ways to speed that up, but even at 1.5-3 seconds for
a moderate sized function, I find this quite useful, and you might
as well.

`lein-zprint` will output the single line you need for such a script,
and will figure out the classpath and all of the options you need.

Here are the detailed instructions:

  1. Install [planck][planck-url] or [lumo][lumo-url].  

  I have tried this with planck v2, and lumo 1.2.0.  Do not
  use earlier versions.
  
  Why choose one over the other?  If you already have one or the
  other, I'd use that.  I initially implemented this with planck,
  and later also implemented it with lumo.  In my environment, in
  the not terribly rigorous testing I've done, lumo is almost twice
  as fast as planck -- but I found the planck version perfectly
  usable before I implemented the lumo version.   That said, I now
  use the zprint-filter using the JVM, documented over in zprint
  itself, which is slightly faster than either. But both planck
  and lumo are are easy to install and use, and I highly recommend 
  both projects

  2. Figure out where you want to put the script so that it is on
  your path.  I put mine in ~/bin, for example, since that directory
  is already in my path.  It doesn't matter where you put it, as
  long as the directory you put it in is on your path.

  3. Create a directory for the cache of Javascript files that
  planck and lumo use to startup quickly.  This is the `<cache-directory>`
  that you will use in the next step. Planck or lumo will compile
  the Clojurescript code once, and cache it in the directory that
  you provide.  This cacheing is very important, as it takes minutes
  to compile the code to Javascript, but only a second or so to
  load it up.  This directory must already exist when specified in
  the `lein zprint :planck-cmd-line` or `lein zprint :lumo-cmd-line`
  directive below.

  4. Go to any project which has `lein-zprint` as a plugin, and
  type: `lein zprint :planck-cmd-line <cache-directory>` or
  `lein zprint :lumo-cmd-line <cache-directory>`.

  You should see a single line come out which starts with `planck`
  or `lumo`,
  and looks something like this (of course your paths will be
  different and the versions of the jars will probably be different):
  
  Planck:

  ```
planck -s -k /Users/kkinnear/bin/planck-cache -c /Users/kkinnear/.m2/repository/zprint/zprint/0.4.3/zprint-0.4.3.jar:/Users/kkinnear/.m2/repository/rewrite-cljs/rewrite-cljs/0.4.4/rewrite-cljs-0.4.4.jar -m zprint.planck
  ```

  Lumo:

  ```
lumo -k /Users/kkinnear/bin/planck-cache -c /Users/kkinnear/.m2/repository/zprint/zprint/0.4.3/zprint-0.4.3.jar:/Users/kkinnear/.m2/repository/rewrite-cljs/rewrite-cljs/0.4.4/rewrite-cljs-0.4.4.jar -m zprint.lumo
  ```

  5. Once you get to that point, type the command line again, and
  pipe the output into a file whose name will be the name of your
  filter.  In the example below, I have chosen "zp" as the filename
  of the pretty print filter:

  ```
lein zprint :planck-cmd-line <cache-directory> >zp
  ```

  or

  ```
lein zprint :lumo-cmd-line <cache-directory> >zp
  ```

  6. Move the file created above into the directory that you
  identified in Step #3, above, and make it executable:

  ```
chmod +x zp
  ```

  7. Compile the Clojurescript and also test the script.  

  Note --
  the *first* time it runs, it will take about two minutes or
  possibly even more to run.  This is how long the initial compile
  takes.  After that, it will run in 1.5-3 seconds each time, since
  planck or lumo will cache the Javascript in the directory that you have
  given.  It will seem like a lot longer than two minutes for it
  to compile the first time -- I was surprised when I actually
  measured it.  I would have said it was at least 4 minutes, maybe
  more.  Seems like forever!  Eventually, it should compile.

  The filter reads from stdin, and writes to stdout.  A simple way
  to test it (and have it compile) is to create a short file with
  a single string in it:

  ```
echo "\"hello world\"" >> test.clj
  ```

  and then to try out the zprint filter on it:

  ```
zp < test.clj
"hello world"
  ```

  If you get the string that you put in out of it, then it is
  working.  It will not be quick the first time, but if you run it
  again, it should be only 1.5-3 seconds.

  7. Figure out how to get your editor to take a bunch of text and
  pipe it through a filter.  A commonly used filter is `fmt`, so
  you might look for how you can pipe some text through `fmt`. The
  script you have created can be used the same way, only instead
  of `fmt`, you give it `zp`.  You may also want to determine how
  to select the text between two matching parentheses.  In any case,
  do whatever it necessary with your editor to pipe a complete
  Clojure(script) function through a unix-style filter.

    * In vim, you can type `!a(zp` if you have created the script to
    be called `zp`.  If you are sitting on the top level left parenthesis
    (which you should be), then this will pipe everything to the balanced
    right parenthesis through `zp`, formatting an entire function.
    There are other ways to mark all of the text between parentheses in
    vim.

    * Emacs seems to have several ways to do this, including moving
    to the top level of an s-expression before sending all of the
    information off to an external program.  I'm not even going to
    try to sort through the various options and recommend one particular
    one.

  If you specify the filter as `zp` (or whatever you have called
  it), you should get the text you have replaced with a nicely
  formatted output.
  
  Note: you should do an entire function, as if you start in the
  middle, zprint has no way to know the width of the output you want.

  Note: Some editors may not recognize that parentheses in comments
  don't "count", and so if you have unbalanced parentheses in comments
  then if you use a command that gathers up all of the text to the balanced
  right parenthesis you may not send all of the Clojure(script) function
  to zprint, in which case zprint will output an error which will end up
  back in your text file along with the unmodified input.  You can remove 
  the error with the editor, or just undo the change.

  Additionally, the default width for zprint is 80 columns.  If you
  routinely expect your code to be more than 80 columns, you can
  configure zprint in several ways, see below.

That's the basics of creating and installing the zprint filter.

[planck-url]: https://github.com/mfikes/planck
[lumo-url]: https://github.com/anmonteiro/lumo


### Configuring the zprint filter

There are three ways to configure the zprint filter.   Environment
variables cannot be used.

  * Use a `~/.zprintrc` configuration file.  See the zprint
  readme for how that works -- Clojurescript zprint using planck 
  or lumo will support the `~/.zprintrc` file.

  * Add an options map to the single line in the script file.  For
  instance (this is a very long line, scroll right all the way):

  ```
planck -s -k /Users/kkinnear/bin/planck-cache -c /Users/kkinnear/.m2/repository/zprint/zprint/0.4.3/zprint-0.4.3.jar:/Users/kkinnear/.m2/repository/rewrite-cljs/rewrite-cljs/0.4.4/rewrite-cljs-0.4.4.jar -m zprint.planck '{:width 90}'
  ```
  would cause all of the code piped through this script to be formatted for
  a width of 90, and not 80.  Don't forget the single quotes!

  * Use `;!zprint {...}` directives inside of a source file.
  This only works if the directive is co-located with the source of
  the function, and you pipe them through the filter together.  But
  the filter will honor any `;!zprint {...}` directives that it encounters,
  just like `lein zprint` will honor them in the files that it processes.

If you have several ways that you like to format functions, you can
have several executable scripts with a different option map in each
one.  They can all share the same planck or lumo Javascript cache,
and you can use the one that makes the most sense for this or that
function.  You might have one that does default zprint, and another
that has an option map of `{:style :justified}` which you would use
when you wanted to format a function using the justified approach.
Or just if you wanted to see if a function looked better that way
as an experiment.  You could have as many different one-line
zprint-planck or zprint-lumo scripts as you could remember to use,
all sharing the same Javascript cache (as long as they all have the
same classpath in them).

### Upgrading the zprint filter

If you upgrade `lein-zprint` or `zprint` itself to a version beyond
that which was current when you first created the filter command file,
you do not have to go through the process all over again.  You can
simply edit the script file to have the version of the `zprint` library
that is current at the time.  You have to have downloaded this version
as a dependency of some project, as the script does no dependency downloads.
But if you have used a newer version of `lein-zprint` or `zprint`, then you
can put the newer version of `zprint` or the version of `zprint` used by
an updated `lein-zprint` in the script file, and it will use the newer version.

If you are using `lein-zprint`, and you type:

```
lein zprint :explain
```

lots of output will be produced.
The version of zprint used by `lein-zprint` will show up as the value
of the `:version` key:

```
{...
 :user-fn-map {},
 :vector {:indent 1, :wrap-after-multi? true, :wrap-coll? true, :wrap? true},
 :version "zprint-0.4.3",
 :width 80}
```

If this is a later version than you have in the `zp` script you created,
then you might want to edit the script to upgrade.

It will, of course, take another two minutes or so to compile the first
time you use the script after you edit the script to reflect
the newer version of zprint to which you wish to upgrade.

NOTE: `lumo` as of version 1.2.0 does not invalidate the cache based on
the dates of the file, so that when upgrading you probably have to delete
all of the files in the cache directory that you gave lumo.  I expect that
over time this will change, but you would never be incorrect to explicitly
delete the cache files when you upgrade.

NOTE: If you have multiple scripts sharing the same cache (which is the
reasonable way to do this), then you _must_ upgrade them all at the same
time, otherwise you will constantly be invalidating the cache and reloading
it every time you use a script with a different version of zprint. 

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

  * The `$HOME/.zprintrc` file, containing a zprint options map, as described in
  the zprint readme.  This configuration information will be used for all invocations
  of the zprint library by this user -- including lein zprint and repl calls.

  * __DEPRECATED__: Environment variables, as described in the zprint readme. 

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

Copyright © 2016-2017 Kim Kinnear

Distributed under the MIT License.  See the file LICENSE for details.
