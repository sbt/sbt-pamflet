package sbtpamflet
package compiler

import java.io.File
import scala.collection.concurrent.TrieMap
import xsbti.compile.{ ScalaInstance, ClasspathOptions }
import sbt.{ Logger => SbtLogger, Level }
import sbt.compiler.CompilerArguments
import xsbtpamfleti.{ ConsoleInterface, ConsoleResponse, Logger, F0 }

case class ConsoleSession(
    name: String,
    scalaInstance: ScalaInstance,
    compilerBridge: CompilerBridgeInstance,
    scalacOptions: Seq[String],
    bootClasspathString: String,
    classpathString: String,
    log: Logger) {
  lazy val console: ConsoleInterface = (new Console(scalaInstance, compilerBridge)).
    console(scalacOptions, bootClasspathString, classpathString,
      "", "", Array(), Array(), log)
  def reset(): Unit =
    console.reset()
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
  def clear(): Unit = sessions.clear()
  
  def apply(tag: String, compilerBridge: CompilerBridgeInstance, customClasspath: List[File], scalaInstance: ScalaInstance,
    classpathOptions: ClasspathOptions, scalacOptions: Seq[String], log: SbtLogger): ConsoleSession =
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
      val s0 = sessions.getOrElseUpdate(name,
        ConsoleSession(name, scalaInstance, compilerBridge, scalacOptions, bootClasspath, classpathString, log)
      )
      if (options.reset) {
        s0.reset()
      }
      s0
    }
  implicit def toXpamfletiLogger(log: SbtLogger): Logger =
    new Logger {
      def debug(msg: F0[String]): Unit = log.log(Level.Debug, msg.apply)
      def warn(msg: F0[String]): Unit = log.log(Level.Warn, msg.apply)
      def info(msg: F0[String]): Unit = log.log(Level.Info, msg.apply)
      def error(msg: F0[String]): Unit = log.log(Level.Error, msg.apply)
      def trace(msg: F0[Throwable]) = log.trace(msg.apply)    
    }
}
