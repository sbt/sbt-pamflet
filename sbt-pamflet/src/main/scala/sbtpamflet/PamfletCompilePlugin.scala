package sbtpamflet

import sbt._
import Keys._
import PamfletPlugin.autoImport._

object PamfletCompilePlugin extends AutoPlugin {
  override def requires = PamfletPlugin
  override def trigger = AllRequirements

  override def projectSettings = pamfletCompileSettings
  lazy val pamfletCompileSettings = inConfig(Pamflet)(basePamfletCompileSettings)
  lazy val basePamfletCompileSettings = Seq(
    pfOnError in compile := PfOnError.Halt,
    pfFencePlugins += {
      val cp = fullClasspath.value.toList
      val co = compileOrder.value
      val cs = compilers.value
      val cis = compileIncSetup.value
      new CompileFence {
        def errorHandling = (pfOnError in compile).value
        override def customClasspath = cp map { _.data }
        def scalacOptions = Keys.scalacOptions.value
        def javacOptions = Keys.javacOptions.value
        def maxErrors = Keys.maxErrors.value
        def sourcePositionMappers = Keys.sourcePositionMappers.value
        def compileOrder = co
        def compilers = cs
        def compileIncSetup = cis
        def log = sLog.value
      }
    }
  )
}
