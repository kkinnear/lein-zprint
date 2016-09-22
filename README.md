# lein-zprint

A Leiningen plugin that will use the zprint library to format your Clojure source
files.  Zprint reformats Clojure source files from scratch, completely ignoring
all existing line breaks and white space in the file.  

The zprint library itself is highly configurable, allowing you to tune the formatting.

## Usage

It is pretty straightforward to use lein zprint.

Place `[lein-zprint "0.1.1"]` into the `:plugins` vector of your project.clj.

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

See [zprint]() for information on how to configure the pretty printing performed
by the zprint library.

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

  Again, you could change the width for the project this way (as well as configure
  any of the myriad aspects of the zprint library):

    ```
    ...
    :zprint {:width 90}
    ...
    ```
  This only affects the configuration for lein zprint, it is not recognized by
  repl czprint or czprint-fn calls.

  * Arguments on the command line of lein zprint.

  The first argument to lein zprint can be a number or an options map.  If it is a
  number, it is used as the width.  If it is an options map, it is used to configure
  zprint.  If the first argument is a number, the second argument can be an options
  map.  Note that an options map must be surrounded by double quotes.

  Here is yet another way to change the width to 90:

  ```
  lein zprint 90 src/myproject/*.clj
  ```

  and one other (of course this options map can contain anything allowed to configure
  zprint -- see the zprint readme).

  ```
  lein zprint "{:width 90}" src/myproject/*.clj
  ```

There are so many ways to configure zprint (and even more for lein zprint), that you
might end up wondering what configuration you are actually using.  To find out,
you can place the special token `:explain` anywhere in the arguments list, and lein
zprint will output the current options map with information explaining where all
of the non-default values came from.  Thus, 

```
lein zprint 95 :explain src/myproject/*.clj
```

will set the width to 95, output the options map to standard out, and then
format all of the .clj files in src/myproject.

Finally, if you forget any of this, you can always type:

```
lein zprint :help
```

which will output some helpful reminders.

## Support

If you have problems with lein zprint, please create an issue.  Please include
the output from:

```
lein zprint :explain
```

as well as the source file (or fragment thereof) that is a concern, and
please show what lein zprint produced and explain how it differed 
from what you expected.

## License

Copyright © 2016 Kim Kinnear

Distributed under the MIT License.  See the file LICENSE for details.
