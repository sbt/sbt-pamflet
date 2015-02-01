package sbtpamflet

import sbt._
import pamflet.FencePlugin

trait PamfletKeys {
  lazy val pf = taskKey[Unit]("Preview Pamflet")
  lazy val pfWrite = taskKey[Vector[File]]("Write Pamflet")
  lazy val pfFencePlugins = taskKey[Seq[FencePlugin]]("Pamflet fence plugins")
  lazy val pfOnError = taskKey[PfOnError]("Error handling")
}
object PamfletKeys extends PamfletKeys {}
