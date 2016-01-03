lazy val scalaCompiler = Def.setting { "org.scala-lang" % "scala-compiler" % scalaVersion.value }
lazy val pamflet = "net.databinder" %% "pamflet-library" % "0.7.0-SNAPSHOT"
lazy val scala2106 = "2.10.6"
lazy val scala2117 = "2.11.7"
lazy val pluginDeps = Seq(pamflet)

lazy val commonSettings = Seq(
  organization := "com.eed3si9n",
  version := "0.1.0-SNAPSHOT",
  publishMavenStyle := false,
  publishTo := {
    if (isSnapshot.value) Some(Resolver.sbtPluginRepo("snapshots"))
    else Some(Resolver.sbtPluginRepo("releases"))
  },
  crossScalaVersions := Seq("2.10.4"),
  credentials += Credentials(Path.userHome / ".ivy2" / ".sbtcredentials")
)

lazy val root: Project = (project in file(".")).
  aggregate(sbtPamflet, interface, precompiled2106, precompiled2117, compiler).
  settings(commonSettings: _*).
  settings(
    publishArtifact := false
  )

lazy val sbtPamflet: Project = (project in file("sbt-pamflet")).
  dependsOn(compiler).
  settings(commonSettings ++ buildInfoSettings: _*).
  settings(
    sbtPlugin := true,
    name := "sbt-pamflet",
    description := "sbt plugin for Pamflet",
    libraryDependencies ++= pluginDeps,
    sourceGenerators in Compile <+= buildInfo,
    buildInfoKeys := Seq[BuildInfoKey](organization, name, version,
      "precompiledVersions" -> Seq(scala2106, scala2117)),
    buildInfoPackage := "sbtpamflet"
  )

// This project relies on bridge via reflection
lazy val compiler: Project = (project in file("compiler")).
  dependsOn(interface).
  settings(commonSettings: _*).
  settings(
    sbtPlugin := true,
    name := "Compiler",
    description := "Abstraction around console"
  )

// defines Java structure across Scala versions.
lazy val interface: Project = (project in file("interface")).
  settings(commonSettings: _*).
  settings(
    autoScalaLibrary := false,
    crossPaths := false,
    name := "console-interface",
    description := "Java structure for console across Scala versions",
    exportJars := true
  )

lazy val precompiled2106 = precompiledCompilerBridge(scala2106)
lazy val precompiled2117 = precompiledCompilerBridge(scala2117)

def precompiledCompilerBridge(scalav: String): Project = Project(id = StringUtilities.normalize("Compiler Bridge " + scalav.replace('.', '_')),
    base = file("compiler-bridge")).
  dependsOn(interface).
  settings(commonSettings: _*).
  settings(
    name := "Compiler Bridge " + scalav.replace('.', '_'),
    scalaVersion := scalav,
    crossScalaVersions := Seq(scalav),
    scalaHome := None,
    exportJars := true,
    crossPaths := false,
    // crossVersion := CrossVersion.full,
    libraryDependencies += scalaCompiler.value % "provided",
    target := {
      target.value / ("precompiled_" + scalav)
    }
  )
