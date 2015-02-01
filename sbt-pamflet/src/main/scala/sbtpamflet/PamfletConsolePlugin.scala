package sbtpamflet

import sbt._
import Keys._
import PamfletPlugin.autoImport._

object PamfletConsolePlugin extends AutoPlugin {
  override def requires = PamfletPlugin
  override def trigger = AllRequirements

  object autoImport {
  }

  override def projectSettings = pamfletConsoleSettings
  lazy val pamfletConsoleSettings = inConfig(Pamflet)(basePamfletConsoleSettings)
  lazy val basePamfletConsoleSettings = Seq(
    pfOnError in console := PfOnError.PrintError,
    pfFencePlugins += {
      val cp = (fullClasspath in console).value.toList
      val si = (scalaInstance in console).value
      val so = (scalacOptions in console).value
      new ConsoleFence {
        def errorHandling = (pfOnError in console).value
        override def customClasspath = cp map { _.data }
        override def scalaInstance = si
        def scalacOptions = so
        def log = sLog.value
      }
    }
  )
}
