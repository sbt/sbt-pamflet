sbt-pamflet
===========

This is an sbt plugin that integrates [Pamflet] to the build.
In addition, it provides `console` and `compile` fence plugins to your pamflet.

usage
-----

From sbt shell:

```
> pf
```

This works like normal Pamflet preview mode. To write your pamflet run:

```
> pfWrite
```

compile fence plugin
--------------------

`PamfletCompilePlugin` introduces `compile` fence plugin.
Substitute normal `scala` fence code block with `compile`:

    ```compile
    class nonsense }
    ```

By default, this fence plugin will fail illegal Scala code:

```
[error] :1: Unmatched closing brace '}' ignored here
[error] class nonsense }
[error]                ^
[error] one error found
```

To use third-party library include it into `Pamflet` configuration in `build.sbt`:

```scala
lazy val dispatchV = "0.11.2"
lazy val dispatch = "net.databinder.dispatch" %% "dispatch-core" % dispatchV

val root = (project in file(".")).
  settings(
    libraryDependencies += dispatch % Pamflet
  )
```

console fence plugin
--------------------

(currently supports Scala 2.10.4 and 2.11.5 only)

`PamfletConsolePlugin` introduces `console` fence plugin.
Write out REPL promps in your pamflet, it will automatically exapand to an interaction:


    ```console
    scala> :paste
    class Foo
    object Foo
    scala> Foo
    ```

This exapands out as follows:

```scala
scala> :paste
// Entering paste mode (ctrl-D to finish)
class Foo
object Foo

// Exiting paste mode, now interpreting.

defined class Foo
defined module Foo

scala> Foo
res0: Foo.type = Foo$@7b020812
```

  [Pamflet]: http://www.foundweekends.org/pamflet/
