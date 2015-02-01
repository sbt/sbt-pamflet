package xsbtpamflet

import xsbti.Logger

class ConsoleFactory extends xsbtpamfleti.ConsoleFactory {
  def createConsole(args: Array[String], bootClasspathString: String,
    classpathString: String, initialCommands: String, cleanupCommands: String,
    loader: ClassLoader, bindNames: Array[String], bindValues: Array[AnyRef],
    log: Logger): xsbtpamfleti.ConsoleInterface =
    new ConsoleInterface(args, bootClasspathString, classpathString,
      initialCommands, cleanupCommands, loader, bindNames, bindValues, log)
}
