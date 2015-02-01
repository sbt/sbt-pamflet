package sbtpamflet
package compiler

import java.io.File
import scala.collection.concurrent.TrieMap
import xsbti.compile.{ ScalaInstance, ClasspathOptions }
import sbt.Logger
import sbt.compiler.CompilerArguments
import xsbtpamflet.ConsoleInterface
import xsbtpamfleti.ConsoleResponse

case class ConsoleSession(
    name: String,
    scalacOptions: Seq[String],
    bootClasspathString: String,
    classpathString: String,
    log: Logger) {
  lazy val console: ConsoleInterface = new ConsoleInterface(
    scalacOptions.toArray, bootClasspathString, classpathString,
    "", "", // initialCommands, cleanupCommands
    None.orNull, Array(), Array(), log
  )
  def interpret(request: ConsoleRequest): ConsoleResponse =
    interpret(request.input.mkString("\n"))
  def interpret(line: String): ConsoleResponse =
    try {
      console.interpret(line, false)
    } catch {
      case e: Throwable =>
        throw e
    }
}

object ConsoleSession {
  val sessions: TrieMap[String, ConsoleSession] = TrieMap()

  def apply(tag: String, customClasspath: List[File], scalaInstance: ScalaInstance,
    classpathOptions: ClasspathOptions, scalacOptions: Seq[String], log: Logger): ConsoleSession =
    {
      // From AnalyzingCompiler
      def consoleClasspaths(classpath: Seq[File]): (String, String) =
        {
          val arguments = new CompilerArguments(scalaInstance, classpathOptions)
          val classpathString = CompilerArguments.absString(arguments.finishClasspath(classpath))
          val bootClasspath = if (classpathOptions.autoBoot) arguments.createBootClasspathFor(classpath) else ""
          (classpathString, bootClasspath)
        }
      val options = ConsoleSessionOption.parse(tag)
      val (classpathString, bootClasspath) = consoleClasspaths(customClasspath)
      val name = "_"
      if (options.fresh) {
        sessions.remove(name)
      }
      sessions.getOrElseUpdate(name,
        ConsoleSession(name, scalacOptions, bootClasspath, classpathString, log)
      )
    }
}
