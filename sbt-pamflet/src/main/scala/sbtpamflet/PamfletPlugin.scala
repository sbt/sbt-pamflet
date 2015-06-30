package sbtpamflet

import pamflet.{CachedFileStorage, FileStorage, Preview, Produce, FencePlugin}

import sbt._
import Keys._

object PamfletPlugin extends AutoPlugin {
  override def requires = plugins.JvmPlugin
  override def trigger = AllRequirements

  val autoImport = new PamfletKeys {
    val Pamflet = config("pamflet") extend Compile
    val PamfletTool = config("pamflet-tool")
    val PfOnError = sbtpamflet.PfOnError
  }
  import autoImport._
  override def projectSettings = pamfletSettings

  lazy val pamfletSettings = pamfletConfigurationSettings ++ inConfig(Pamflet)(basePamfletSettings)
  lazy val pamfletConfigurationSettings = Seq(
    ivyConfigurations ++= Seq(Pamflet, PamfletTool),
    pf := (pf in Pamflet).value,
    pfWrite := (pfWrite in Pamflet).value
  ) ++ inConfig(Pamflet)(Defaults.configSettings)

  lazy val basePamfletSettings = Seq(
    pf := {
      val dir = (sourceDirectory in pf).value
      val log = sLog.value
      val ps = pfFencePlugins.value
      Preview(storage(dir, ps.toList).globalized).run { server =>
        unfiltered.util.Browser.open(
          "http://127.0.0.1:%d/".format(server.portBindings.head.port)
        )
        log.info(s"Previewing `$dir`")
      }
    },
    aggregate in pf := false,
    pfWrite := {
      val dir = (sourceDirectory in pfWrite).value
      val out = (target in pfWrite).value
      val log = sLog.value
      val ps = pfFencePlugins.value
      IO.createDirectory(out)
      Produce(storage(dir, ps.toList).globalized, out)
      log.info(s"Wrote pamflet to $out")
      (out ** (-DirectoryFilter)).get.toVector
    },
    aggregate in pfWrite := false,
    sourceDirectory in pfWrite := (sourceDirectory in pf).value,
    sourceDirectory in pf := baseDirectory.value / "docs",
    target in pfWrite := (target in pf).value,
    target in pf := target.value / "www",
    pfFencePlugins := Seq()
  )

  private def storage(dir: File, ps: List[FencePlugin]) = CachedFileStorage(dir, ps)
}
