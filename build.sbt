lazy val pamflet = "net.databinder" %% "pamflet-library" % "0.7.0-SNAPSHOT"
lazy val pluginDeps = Seq(pamflet)

lazy val commonSettings = Seq(
  organization := "com.eed3si9n",
  version := "0.1.0-SNAPSHOT",
  publishMavenStyle := false,
  publishTo := {
    if (isSnapshot.value) Some(Resolver.sbtPluginRepo("snapshots"))
    else Some(Resolver.sbtPluginRepo("releases"))
  },
  credentials += Credentials(Path.userHome / ".ivy2" / ".sbtcredentials")
)

lazy val root: Project = (project in file(".")).
  aggregate(sbtPamflet, interface, compileBridge, compiler).
  settings(commonSettings: _*).
  settings(
    publishArtifact := false
  )

lazy val sbtPamflet: Project = (project in file("sbt-pamflet")).
  dependsOn(compiler).
  settings(commonSettings: _*).
  settings(
    sbtPlugin := true,
    name := "sbt-pamflet",
    description := "sbt plugin for Pamflet",
    libraryDependencies ++= pluginDeps
  )

lazy val compiler: Project = (project in file("compiler")).
  dependsOn(interface, compileBridge).
  settings(commonSettings: _*).
  settings(
    sbtPlugin := true,
    name := "Console",
    description := "Abstraction around console"
  )

// defines Java structure across Scala versions.
lazy val interface: Project = (project in file("interface")).
  settings(commonSettings: _*).
  settings(
    sbtPlugin := true,
    crossPaths := false,
    name := "console-interface",
    description := "Java structure for console across Scala versions",
    exportJars := true
  )

lazy val compileBridge: Project = (project in file("compile-bridge")).
  dependsOn(interface).
  settings(commonSettings: _*).
  settings(
    sbtPlugin := true,
    name := "compile-bridge",
    exportJars := true
  )
