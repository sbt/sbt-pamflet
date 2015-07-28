package sbtpamflet

import sbt._
import pamflet.FencePlugin
import sbtpamflet.compiler.CompilerBridgeInstance

trait PamfletKeys {
  lazy val pf = taskKey[Unit]("Preview Pamflet")
  lazy val pfWrite = taskKey[Vector[File]]("Write Pamflet")
  lazy val pfFencePlugins = taskKey[Seq[FencePlugin]]("Pamflet fence plugins")
  lazy val pfCompilerBridge = taskKey[CompilerBridgeInstance]("Console instance wrapper")
}
object PamfletKeys extends PamfletKeys {}
