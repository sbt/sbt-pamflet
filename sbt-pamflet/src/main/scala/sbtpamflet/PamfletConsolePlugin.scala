package sbtpamflet

import sbt._
import Keys._
import PamfletPlugin.autoImport._
import sbtpamflet.compiler.CompilerBridgeInstance

object PamfletConsolePlugin extends AutoPlugin {
  override def requires = PamfletPlugin
  override def trigger = AllRequirements

  object autoImport {
  }

  override def projectSettings = pamfletConsoleSettings
  lazy val pamfletConsoleSettings = compilerBridgeSettings ++ inConfig(Pamflet)(basePamfletConsoleSettings)
  
  private def mkName(sv: String): String = "compiler-bridge-" + sv.replace('.', '_')

  lazy val compilerBridgeSettings = Seq(
    libraryDependencies ++= BuildInfo.precompiledVersions map { sv =>
      BuildInfo.organization % mkName(sv) % BuildInfo.version % PamfletTool
    },
    pfCompilerBridge := {
      val si = (scalaInstance in console).value
      val compileReport = update.value.configuration(PamfletTool.name) getOrElse sys.error("Report not found")
      def files(id: String) =
        for {
          m <- compileReport.modules if m.module.name == id
          (art, file) <- m.artifacts if art.`type` == Artifact.DefaultType
        } yield file
      def file(id: String) = files(id).headOption getOrElse sys.error(s"Missing ${id}.jar")
      val bridgeJar = file(mkName(si.actualVersion))
      CompilerBridgeInstance(si.version, bridgeJar)
    }
  )

  lazy val basePamfletConsoleSettings = Seq(
    pfFencePlugins += {
      val cp = (fullClasspath in console).value.toList
      val si = (scalaInstance in console).value
      val so = (scalacOptions in console).value
      new ConsoleFence {
        def compilerBridge = pfCompilerBridge.value
        override def customClasspath = cp map { _.data }
        override def scalaInstance = si
        def scalacOptions = so
        def log = sLog.value
      }
    }
  )
}
